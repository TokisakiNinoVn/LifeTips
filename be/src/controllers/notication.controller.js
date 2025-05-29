const db = require('../config/db.config');
const { HTTP_STATUS } = require('../constants/status-code');
const AppError = require('../utils/app-error');

// Lấy tất cả thông báo của người dùng
exports.getAllNotifications = async (req, res, next) => {
    try {
        const userId = req.user.id;
        
        // Truy vấn để lấy tất cả thông báo của người dùng
        // await db.pool.execute
        const [notifications] = await db.pool.execute(
            'SELECT * FROM notifications ORDER BY created_at DESC'
        );

        return res.status(HTTP_STATUS.OK).json({
            status: 'success',
            data: notifications,
        });
    } catch (error) {
        return next(new AppError('Failed to fetch notifications', HTTP_STATUS.INTERNAL_SERVER_ERROR));
    }
};