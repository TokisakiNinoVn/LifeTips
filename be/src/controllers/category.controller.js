const db = require('../config/db.config');
const { HTTP_STATUS } = require('../constants/status-code');
const AppError = require('../utils/app-error');

exports.getAllCategory = async (req, res, next) => { 
    try {
        const [rows] = await db.pool.execute('SELECT * FROM categories');
        return res.status(HTTP_STATUS.OK).json({
            status: 'success',
            message: 'Get All categories post successfully',
            data: rows // Use 'rows' instead of 'post'
        });
    } catch (error) {
        return next(new AppError(HTTP_STATUS.INTERNAL_SERVER_ERROR, 'fail', error.message, []));
    }
}