// file: src/controllers/post.controller.js

const path = require('path');
const fs = require('fs')

const db = require('../config/db.config');
const { HTTP_STATUS } = require('../constants/status-code');
const AppError = require('../utils/app-error');
const { getPostFiles, getUserById, getPostCategories } = require('../helper/post.helper');
const { console } = require('inspector');

exports.update = async (req, res, next) => {
    const { title, content, categoryId } = req.body;
    const idPost = req.params.id;
    const userId = req.user.id;

    try {
        // Update the post in the tips_post table
        const [postCheck] = await db.pool.execute(`SELECT id FROM tips_post WHERE id = ? AND user_id = ?`, [idPost, userId]);
        if (postCheck.length === 0) {
            return res.status(HTTP_STATUS.NOT_FOUND).json({
                status: 'fail',
                message: 'Post not found or you are not authorized to update it'
            });
        }
        await db.pool.execute(`UPDATE tips_post SET title = ?, content = ?, updated_at = NOW(), category_id = ? WHERE id = ? AND user_id = ?`, [title, content, categoryId, idPost, userId]);
        
        res.status(200).json({
            code: 200,
            status: 'success',
            message: 'Post updated successfully',
        });
    } catch (error) {
        console.error('Error updating post:', error);
        return res.status(500).json({
            status: 'fail',
            message: error.message
        });
    }
};

exports.delete = async (req, res, next) => {
    const { id } = req.params;
    const userId = req.user.id; // Get the user ID from the request

    try {
        // 1. Check if the post exists and belongs to the user
        const [postCheck] = await db.pool.execute(`SELECT id FROM tips_post WHERE id = ? AND user_id = ?`, [id, userId]);

        if (postCheck.length === 0) {
            return next(new AppError(HTTP_STATUS.NOT_FOUND, 'fail', 'Post not found or you are not authorized to delete it', []), req, res, next);
        }

        // 2. Delete category associations from category_post table
        // await db.pool.execute(`DELETE FROM category_post WHERE tipspost_id = ?`, [id]);

        // 3. Delete files associated with the post
        const [files] = await db.pool.execute(`SELECT url FROM files WHERE tipspost_id = ?`, [id]);
        // 4. Delete likes and saves associated with the post
        await db.pool.execute(`DELETE FROM likes_saves WHERE tippost_id = ?`, [id]);
        // 5. Delete comments associated with the post
        await db.pool.execute(`DELETE FROM comments WHERE tippost_id = ?`, [id]);
        // 6. Delete notifications associated with the post
        await db.pool.execute(`DELETE FROM notifications WHERE post_id = ?`, [id]);

        for (const file of files) { // Use a for...of loop for asynchronous operations
            const filePath = path.join(__dirname, '..', file.url);
            try {
                if (fs.existsSync(filePath)) {
                    fs.unlinkSync(filePath);
                }
            } catch (unlinkError) {
                console.error(`Error deleting file ${filePath}:`, unlinkError);
            }
        }

        await db.pool.execute(`DELETE FROM files WHERE tipspost_id = ?`, [id]);

        // 4. Delete the post itself
        await db.pool.execute(`DELETE FROM tips_post WHERE id = ? AND user_id = ?`, [id, userId]);


        return next({ statusCode: HTTP_STATUS.OK, message: 'Post and related data deleted successfully', data: {} }, req, res, next);

    } catch (error) {
        console.error('Error deleting post:', error);
        return next(new AppError(HTTP_STATUS.INTERNAL_SERVER_ERROR, 'fail', error.message, []), req, res, next);
    }
};

exports.getById = async (req, res, next) => {
    const { id } = req.params;

    try {
        const [postResult] = await db.pool.execute(`
            SELECT * FROM tips_post
            WHERE id = ?
        `, [id]);

        if (postResult.length === 0) {
            return next(new AppError(404, 'fail', 'Post not found', []), req, res, next);
        }

        const post = postResult[0];

        const [files, user, categories] = await Promise.all([
            getPostFiles(id),
            getUserById(post.user_id),
            getPostCategories(id)
        ]);

        const postData = {
            ...post,
            filesNormal: files,
            user,
            inforCategoryOfPost: categories
        };

        res.status(200).json({
            code: 200,
            message: 'Post retrieved successfully',
            data: postData
        });
    } catch (error) {
        return next(new AppError(500, 'fail', error.message, []), req, res, next);
    }
};

exports.search = async (req, res, next) => {
    const { keyword } = req.body;
    try {
        // Tìm kiếm bài đăng theo tiêu đề hoặc nội dung
        const sql = `
            SELECT * FROM tips_post
            WHERE title LIKE ? OR content LIKE ? AND is_private = 0
            ORDER BY created_at DESC
        `;
        const [posts] = await db.pool.execute(sql, [`%${keyword}%`, `%${keyword}%`]);

        const postsWithFiles = await Promise.all(
            posts.map(async (post) => {
                const [files] = await db.pool.execute(
                    `SELECT * FROM files WHERE tipspost_id = ?`,
                    [post.id]
                );

                // Lấy thông tin người đăng bài
                const [userResult] = await db.pool.execute(`
                    SELECT full_name, avatar, bio, created_at, username
                    FROM users
                    WHERE id = ?
                `, [post.user_id]);
                return { ...post, files, user: userResult[0] };
            })
        );

        res.status(HTTP_STATUS.OK).json({
            code: HTTP_STATUS.OK,
            totalDocs: posts.length,
            data: postsWithFiles,
            message: 'Posts retrieved successfully',
        });
    } catch (error) {
        return next(new AppError(HTTP_STATUS.INTERNAL_SERVER_ERROR, 'fail', error.message, []), req, res, next);
    }
};

exports.getNewPost = async (req, res, next) => {
    try {
        // Lấy 10 bài đăng mới nhất
        const sql = `SELECT * FROM tips_post WHERE is_private = 0 ORDER BY created_at DESC LIMIT 10`;
        const [posts] = await db.pool.execute(sql);

        const postsWithFiles = await Promise.all(
            posts.map(async (post) => {
                const [files] = await db.pool.execute(
                    `SELECT * FROM files WHERE tipspost_id = ?`,
                    [post.id]
                );
                // Lấy thông tin người đăng bài
                const [userResult] = await db.pool.execute(`
                    SELECT full_name, avatar, bio, created_at, username
                    FROM users
                    WHERE id = ?
                `, [post.user_id]);
                return { ...post, files, user: userResult[0] };
            })
        );

        // return next({ status: HTTP_STATUS.OK, message: 'Newest posts retrieved successfully', data: posts }, req, res, next);
        res.status(HTTP_STATUS.OK).json({
            code: HTTP_STATUS.OK,
            totalDocs: posts.length,
            data: postsWithFiles,
            message: 'Newest posts retrieved successfully',
        });
    } catch (error) {
        return next(new AppError(HTTP_STATUS.INTERNAL_SERVER_ERROR, 'fail', error.message, []), req, res, next);
    }
};

exports.getPostSameCategory = async (req, res, next) => {
    const { categoryId } = req.params;
    try {
        // Lấy danh sách bài đăng cùng loại
        const sql = `SELECT * FROM tips_post WHERE category_id = ? AND is_private = 0 ORDER BY created_at DESC`;
        const [postsWithCategoryId] = await db.pool.execute(sql, [categoryId]);
        if (postsWithCategoryId.length === 0) {
            return res.status(HTTP_STATUS.NOT_FOUND).json({
                code: HTTP_STATUS.NOT_FOUND,
                message: 'No posts found for this category',
                data: []
            });
        }
        const postsWithFiles = await Promise.all(
            postsWithCategoryId.map(async (post) => {
                const [files] = await db.pool.execute(
                    `SELECT * FROM files WHERE tipspost_id = ?`,
                    [post.id]
                );
                // Lấy thông tin người đăng bài
                const [userResult] = await db.pool.execute(`
                    SELECT full_name
                    FROM users
                    WHERE id = ?
                `, [post.user_id]);
                return { ...post, files, user: userResult[0] };
            })
        );
        res.status(HTTP_STATUS.OK).json({
            code: HTTP_STATUS.OK,
            totalDocs: postsWithCategoryId.length,
            data: postsWithFiles,
            message: 'Posts of the same type retrieved successfully',
        });
    } catch (error) {
        console.error('Error retrieving posts of the same type:', error);
        return next(new AppError(HTTP_STATUS.INTERNAL_SERVER_ERROR, 'fail', error.message, []), req, res, next);
    }
};

exports.getRandomPost = async (req, res, next) => { 
    try {
        // Lấy 10 bài đăng ngẫu nhiên
        const sql = `SELECT * FROM tips_post WHERE is_private = 0 ORDER BY RAND() LIMIT 10`;
        const [posts] = await db.pool.execute(sql);

        const postsWithFiles = await Promise.all(
            posts.map(async (post) => {
                const [files] = await db.pool.execute(
                    `SELECT * FROM files WHERE tipspost_id = ?`,
                    [post.id]
                );

                // Lấy thông tin danh mục của bài đăng
                const [categories] = await db.pool.execute(`
                    SELECT c.id, c.name, c.description
                    FROM tips_post tp
                    JOIN categories c ON tp.category_id = c.id
                    WHERE tp.id = ?
                `, [post.id]);
                
                post.category = categories.length > 0 ? categories[0] : null;

                // Lấy thông tin người đăng bài
                const [userResult] = await db.pool.execute(`
                    SELECT full_name, avatar
                    FROM users
                    WHERE id = ?
                `, [post.user_id]);
                return { ...post, files, user: userResult[0] };
            })
        );

        res.status(HTTP_STATUS.OK).json({
            code: HTTP_STATUS.OK,
            totalDocs: posts.length,
            data: postsWithFiles,
            message: 'Random posts retrieved successfully',
        });
    } catch (error) {
        return next(new AppError(HTTP_STATUS.INTERNAL_SERVER_ERROR, 'fail', error.message, []), req, res, next);
    }
}

//Get all post of user
exports.getAllPostOfUser = async (req, res, next) => {
    const userId = req.user.id;
    try {
        // Lấy danh sách bài đăng của user
        const sql = `SELECT * FROM tips_post WHERE user_id = ? ORDER BY created_at DESC`;
        const [posts] = await db.pool.execute(sql, [userId]);

        const postsWithFiles = await Promise.all(
            posts.map(async (post) => {
                const [files] = await db.pool.execute(
                    `SELECT * FROM files WHERE tipspost_id = ?`,
                    [post.id]
                );
                return { ...post, files };
            })
        );

        res.status(HTTP_STATUS.OK).json({
            code: HTTP_STATUS.OK,
            totalDocs: posts.length,
            data: postsWithFiles,
            message: 'Posts of the same type retrieved successfully',
        });
    } catch (error) {
        console.error('Error retrieving posts of the same type:', error);
        return next(new AppError(HTTP_STATUS.INTERNAL_SERVER_ERROR, 'fail', error.message, []), req, res, next);
    }
};

exports.createPostWithFile = async (req, res, next) => {
    const { title, content, categoryId, isPrivate } = req.body;
    const userId = req.user.id;
    try {
        let createPostQuery = `
                INSERT INTO tips_post (user_id, title, content, created_at, updated_at, category_id, is_private)
                VALUES (?, ?, ?, NOW(), NOW(), ?, ?)
            `;
        
        const [postResult] = await db.pool.execute(createPostQuery, [
            userId,
            title,
            content,
            categoryId,
            // isPrivate ? 1 : 0 // Chuyển đổi isPrivate sang 1 hoặc 0
            isPrivate
        ]);

        const postId = postResult.insertId;

        // Lưu file
        if (req.files && req.files.length > 0) {
            const insertFileQuery = `
                INSERT INTO files (tipspost_id, url, type_file, created_at)
                VALUES (?, ?, ?, NOW())
            `;

            for (const file of req.files) {
                await db.pool.execute(insertFileQuery, [postId, file.path, file.mimetype]);
            }
        }

        // Lấy name của danh mục
        let categoryName = '';
            const [categoryResult] = await db.pool.execute(`
                SELECT name FROM categories WHERE id = ?
            `, [categoryId]);

        categoryName = categoryResult[0].name;

        // Lấy thông tin người dùng
        const [userResult] = await db.pool.execute(`
            SELECT full_name FROM users WHERE id = ?
        `, [userId]);

        if (isPrivate !== 1) {
            const notificationTitle = 'Đã có mẹo vặt xu hướng mới xuất hiện';
            const notificationContent = `Một mẹo vặt xu hướng mới đã được đăng bởi ${userResult[0].full_name} trong danh mục ${categoryName}`;
            const insertNotificationQuery = `
                INSERT INTO notifications (post_id, user_id, title, content, created_at)
                VALUES (?, ?, ?, ?, NOW())
            `;
            await db.pool.execute(insertNotificationQuery, [postId, userId, notificationTitle, notificationContent]);
        }
        

        res.status(HTTP_STATUS.OK).json({
            message: 'Post with files created successfully',
            status: 'success',
            data: { postId }
        });
    } catch (error) {
        console.error('Error creating post with files:', error);
        return next(new AppError(HTTP_STATUS.INTERNAL_SERVER_ERROR, 'fail', error.message, []), req, res, next);
    }
};

// Lưu bài đăng
exports.savePost = async (req, res, next) => {
    const { postId } = req.body;
    const userId = req.user.id;

    try {
        // Kiểm tra xem bài đăng đã được lưu chưa
        const [checkSaved] = await db.pool.execute(`
            SELECT * FROM likes_saves WHERE user_id = ? AND tippost_id = ?
        `, [userId, postId]);

        if (checkSaved.length > 0) {
            return res.status(HTTP_STATUS.BAD_REQUEST).json({
                status: 'fail',
                message: 'Post already saved'
            });
        }

        // Lưu bài đăng
        await db.pool.execute(`
            INSERT INTO likes_saves (user_id, tippost_id, created_at)
            VALUES (?, ?, NOW())
        `, [userId, postId]);

        res.status(HTTP_STATUS.OK).json({
            status: 'success',
            message: 'Post saved successfully'
        });
    } catch (error) {
        console.error('Error saving post:', error);
        return next(new AppError(HTTP_STATUS.INTERNAL_SERVER_ERROR, 'fail', error.message, []), req, res, next);
    }
};

// Lấy danh sách bài đăng đã lưu
exports.getSavedPost = async (req, res, next) => {
    const userId = req.user.id;

    try {
        // Lấy danh sách bài đăng đã lưu
        const sql = `SELECT * FROM likes_saves WHERE user_id = ? ORDER BY created_at DESC`;
        const [savedPosts] = await db.pool.execute(sql, [userId]);
        if (savedPosts.length === 0) {
            return res.status(HTTP_STATUS.NOT_FOUND).json({
                code: HTTP_STATUS.NOT_FOUND,
                message: 'No saved posts found',
                data: []
            });
        }
        const savedPostsWithDetails = await Promise.all(
            savedPosts.map(async (savedPost) => {
                const [postResult] = await db.pool.execute(`
                    SELECT * FROM tips_post WHERE id = ?
                `, [savedPost.tippost_id]);

                if (postResult.length === 0) {
                    return null; // Bài đăng không tồn tại
                }

                const post = postResult[0];

                const [files] = await db.pool.execute(`
                    SELECT * FROM files WHERE tipspost_id = ?
                `, [post.id]);

                // Lấy thông tin người đăng bài
                const [userResult] = await db.pool.execute(`
                    SELECT full_name, avatar, bio, created_at, username
                    FROM users
                    WHERE id = ?
                `, [post.user_id]);

                return { ...post, files, user: userResult[0] };
            })
        );

        // Lọc ra các bài đăng không hợp lệ (null)
        const validSavedPosts = savedPostsWithDetails.filter(post => post !== null);
        if (validSavedPosts.length === 0) {
            return res.status(HTTP_STATUS.NOT_FOUND).json({
                code: HTTP_STATUS.NOT_FOUND,
                message: 'No valid saved posts found',
                data: []
            });
        }
        // Trả về danh sách bài đăng đã lưu
        res.status(HTTP_STATUS.OK).json({
            code: HTTP_STATUS.OK,
            totalDocs: validSavedPosts.length,
            data: validSavedPosts,
            message: 'Saved posts retrieved successfully',
        });
    } catch (error) {
        return next(new AppError(HTTP_STATUS.INTERNAL_SERVER_ERROR, 'fail', error.message, []), req, res, next);
    }
};

// Xóa bài đăng đã lưu
exports.deleteSavedPost = async (req, res, next) => {
    const { postId } = req.params;
    const userId = req.user.id;

    try {
        // Kiểm tra xem bài đăng đã được lưu chưa
        const [checkSaved] = await db.pool.execute(`
            SELECT * FROM likes_saves WHERE user_id = ? AND tippost_id = ?
        `, [userId, postId]);

        if (checkSaved.length === 0) {
            return res.status(HTTP_STATUS.NOT_FOUND).json({
                status: 'fail',
                message: 'Post not found in saved posts'
            });
        }

        // Xóa bài đăng đã lưu
        await db.pool.execute(`
            DELETE FROM likes_saves WHERE user_id = ? AND tippost_id = ?
        `, [userId, postId]);

        res.status(HTTP_STATUS.OK).json({
            status: 'success',
            message: 'Post unsaved successfully'
        });
    } catch (error) {
        console.error('Error unsaving post:', error);
        return next(new AppError(HTTP_STATUS.INTERNAL_SERVER_ERROR, 'fail', error.message, []), req, res, next);
    }
};