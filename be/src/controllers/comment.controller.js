const db = require('../config/db.config');
const { HTTP_STATUS } = require('../constants/status-code');
const AppError = require('../utils/app-error');

// Lấy tất cả bình luận của một bài đăng
exports.getAllCommentsByPostId = async (req, res, next) => {
    const { postId } = req.params;

    try {
        const [comments] = await db.pool.execute(`
            SELECT c.id, c.content, c.created_at, c.rate, u.full_name
            FROM comments c
            JOIN users u ON c.user_id = u.id
            WHERE c.tippost_id = ?
            ORDER BY c.created_at DESC
        `, [postId]);

        res.status(HTTP_STATUS.OK).json({
            status: "Success",
            data: comments,
        });
    } catch (error) {
        next(new AppError('Failed to fetch comments', HTTP_STATUS.INTERNAL_SERVER_ERROR));
    }
};

// Thêm bình luận mới vào bài đăng
exports.addComment = async (req, res, next) => {
    const { postId } = req.params;
    const { content, rate } = req.body;
    const userId = req.user.id;

    if (!content || !rate) {
        return next(new AppError('Content and rate are required', HTTP_STATUS.BAD_REQUEST));
    }

    try {
        const [result] = await db.pool.execute(`
            INSERT INTO comments (user_id, tippost_id, content, rate)
            VALUES (?, ?, ?, ?)
        `, [userId, postId, content, rate]);

        res.status(HTTP_STATUS.CREATED).json({
            status: "Success",
            message: "Comment added successfully",
            data: {
                id: result.insertId,
                content,
                rate,
                created_at: new Date().toISOString(),
                user_id: userId
            }
        });
    } catch (error) {
        next(new AppError('Failed to add comment', HTTP_STATUS.INTERNAL_SERVER_ERROR));
    }
};