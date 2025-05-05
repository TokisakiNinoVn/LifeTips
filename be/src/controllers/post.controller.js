// file: src/controllers/post.controller.js

const path = require('path');
const fs = require('fs')

const db = require('../config/db.config');
const { HTTP_STATUS } = require('../constants/status-code');
const AppError = require('../utils/app-error');
const { getPostFiles, getUserById, getPostCategories } = require('../helper/post.helper');

exports.create = async (req, res, next) => {
    const { title, content, listCategoryId, isPrivate } = req.body;
    const userId = req.user.id;

    try {
        let createPostQuery;
        if (isPrivate === 1 || isPrivate === '1') { 
            createPostQuery = `
                INSERT INTO tips_post (user_id, title, content, created_at, updated_at, is_private)
                VALUES (?, ?, ?, NOW(), NOW(), 1)
            `;
        } else {
            createPostQuery = `
                INSERT INTO tips_post (user_id, title, content, created_at, updated_at)
                VALUES (?, ?, ?, NOW(), NOW())
            `;
        }

        const [postResult] = await db.pool.execute(createPostQuery, [
            userId,
            title,
            content,
        ]);

        const postId = postResult.insertId;

        // Handle Category Associations
        if (listCategoryId && Array.isArray(listCategoryId) && listCategoryId.length > 0) {
            const insertCategoryQuery = `
                INSERT INTO category_post (tipspost_id, category_id)
                VALUES (?, ?)
            `;

            for (const categoryId of listCategoryId) {
                await db.pool.execute(insertCategoryQuery, [postId, categoryId]);
            }
        }


        res.status(HTTP_STATUS.OK).json({
            message: 'Post created successfully',
            data: { postId }
        });
    } catch (error) {
        return next(new AppError(HTTP_STATUS.INTERNAL_SERVER_ERROR, 'fail', error.message, []), req, res, next);
    }
}

exports.update = async (req, res, next) => {
    const { title, content, listCategoryId } = req.body;
    const idPost = req.params.id;
    const userId = req.user.id;

    try {
        // Update the post in the tips_post table
        const updatePostQuery = `
            UPDATE tips_post
            SET title = ?, content = ?, updated_at = NOW()
            WHERE id = ? AND user_id = ?
        `;
        const [updateResult] = await db.pool.execute(updatePostQuery, [title, content, idPost, userId]);

        if (updateResult.affectedRows === 0) {
            return res.status(404).json({
                status: 'fail',
                message: 'Post not found or you are not authorized to update it'
            });
        }

        // Update category associations
        // 1. Delete existing category associations for this post
        const deleteCategoryQuery = `
            DELETE FROM category_post
            WHERE tipspost_id = ?
        `;
        await db.pool.execute(deleteCategoryQuery, [idPost]);

        // 2. Insert new category associations
        if (listCategoryId && Array.isArray(listCategoryId) && listCategoryId.length > 0) {
            const insertCategoryQuery = `
                INSERT INTO category_post (tipspost_id, category_id)
                VALUES (?, ?)
            `;

            for (const categoryId of listCategoryId) {
                await db.pool.execute(insertCategoryQuery, [idPost, categoryId]);
            }
        }

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
        await db.pool.execute(`DELETE FROM category_post WHERE tipspost_id = ?`, [id]);

        // 3. Delete files associated with the post
        const [files] = await db.pool.execute(`SELECT url FROM files WHERE tipspost_id = ?`, [id]);

        for (const file of files) { // Use a for...of loop for asynchronous operations
            const filePath = path.join(__dirname, '..', 'public', file.url);
            try {
                if (fs.existsSync(filePath)) {
                    fs.unlinkSync(filePath);
                }
            } catch (unlinkError) {
                console.error(`Error deleting file ${filePath}:`, unlinkError);
                // Consider whether to continue deleting other files or abort the operation
                // If aborting, you'll need to rollback the database changes (transaction)
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

// Lấy thông tin bài viết
// exports.getById = async (req, res, next) => {
//     const { id } = req.params;

//     try {
//         // Lấy thông tin bài viết
//         const [postResult] = await db.pool.execute(`
//             SELECT *
//             FROM tips_post
//             WHERE id = ?
//         `, [id]);

//         if (postResult.length === 0) {
//             return next(new AppError(HTTP_STATUS.NOT_FOUND, 'fail', 'Post not found', []), req, res, next);
//         }

//         const post = postResult[0];

//         // Lấy các file đính kèm
//         const [files] = await db.pool.execute(`
//             SELECT url, type_file, created_at
//             FROM files
//             WHERE tipspost_id = ?
//         `, [id]);

//         // Lấy thông tin người đăng bài
//         const [userResult] = await db.pool.execute(`
//             SELECT full_name, avatar, bio, created_at, username
//             FROM users
//             WHERE id = ?
//         `, [post.user_id]);

//         if (userResult.length === 0) {
//             return next(new AppError(HTTP_STATUS.NOT_FOUND, 'fail', 'User not found', []), req, res, next);
//         }

//         let inforCategoryOfPost = [];
//         // Lấy thông tin các danh mục từ bảng categories
//         // kiểu 1 bài đăng sẽ có nhiều category khác nhau ấy, nó lưu ở bảng category_post
//         const [categoriesOfPost] = await db.pool.execute(`
//             SELECT * FROM category_post
//             WHERE tipspost_id = ?
//         `, [id]);

//         if (categoriesOfPost.length > 0) {
//             const categoryIds = categoriesOfPost.map(category => category.category_id).join(',');
//             const [categories] = await db.pool.execute(`
//                 SELECT id, name, description
//                 FROM categories
//                 WHERE id IN (${categoryIds})
//             `);
//             inforCategoryOfPost = categories.map(category => ({
//                 id: category.id,
//                 name: category.name,
//                 description: category.description,
//                 // created_at: category.created_at
//             }));
//         } else {
//             inforCategoryOfPost = [];
//         }


//         if (categoriesOfPost.length === 0) {
//             return next(new AppError(HTTP_STATUS.NOT_FOUND, 'fail', 'No categories found for this post', []), req, res, next);
//         }

//         // Chuẩn bị dữ liệu trả về
//         const postData = {
//             ...post,
//             filesNormal: files || [],
//             user: userResult[0],
//             inforCategoryOfPost: inforCategoryOfPost || []
//         };

//         // Trả về dữ liệu JSON
//         res.status(HTTP_STATUS.OK).json({
//             code: HTTP_STATUS.OK,
//             message: 'Post retrieved successfully',
//             data: postData
//         });
//     } catch (error) {
//         return next(
//             new AppError(HTTP_STATUS.INTERNAL_SERVER_ERROR, 'fail', error.message, []),
//             req, res, next
//         );
//     }
// };
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
            WHERE title LIKE ? OR content LIKE ?
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
        const sql = `SELECT * FROM tips_post ORDER BY created_at DESC LIMIT 10`;
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
    const { categoryId } = req.body;
    try {
        // Lấy danh sách bài đăng cùng loại
        const sql = `
            SELECT * FROM category_post
            WHERE category_id = ?
            ORDER BY created_at DESC
        `;
        const [postsWithCategoryId] = await db.pool.execute(sql, [categoryId]);

        if (postsWithCategoryId.length === 0) {
            return res.status(HTTP_STATUS.NOT_FOUND).json({
                code: HTTP_STATUS.NOT_FOUND,
                message: 'No postsWithCategoryId found for this category',
                data: [],
            });
        }
        const postsWithFiles = await Promise.all(
            postsWithCategoryId.map(async (post) => {
                const [postInfor] = await db.pool.execute(
                    `SELECT * FROM tips_post WHERE id = ?`,
                    [post.tipspost_id]
                );

                const [files] = await db.pool.execute(
                    `SELECT * FROM files WHERE tipspost_id = ?`,
                    [postInfor[0].id]
                );

                // Lấy thông tin người đăng bài
                const [userResult] = await db.pool.execute(`
                    SELECT full_name, avatar, bio, created_at, username
                    FROM users
                    WHERE id = ?
                `, [postInfor[0].user_id]);
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
        const sql = `SELECT * FROM tips_post ORDER BY RAND() LIMIT 10`;
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
    const { id } = req.params;
    try {
        // Lấy danh sách bài đăng của user
        const sql = `SELECT * FROM tips_post WHERE user_id = ? ORDER BY created_at DESC`;
        const [posts] = await db.pool.execute(sql, [id]);

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
        return next(new AppError(HTTP_STATUS.INTERNAL_SERVER_ERROR, 'fail', error.message, []), req, res, next);
    }
};