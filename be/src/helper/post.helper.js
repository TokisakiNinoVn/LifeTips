const db = require('../config/db.config');

// Lấy file đính kèm
exports.getPostFiles = async (tipspost_id) => {
    const [files] = await db.pool.execute(`
        SELECT url, type_file, created_at
        FROM files
        WHERE tipspost_id = ?
    `, [tipspost_id]);
    return files || [];
};

// Lấy thông tin người dùng
exports.getUserById = async (user_id) => {
    const [userResult] = await db.pool.execute(`
        SELECT full_name, avatar, bio, created_at, username
        FROM users
        WHERE id = ?
    `, [user_id]);

    if (userResult.length === 0) {
        throw new Error('User not found');
    }

    return userResult[0];
};

// Lấy danh mục bài viết từ bảng tips_post
exports.getPostCategories = async (tipspost_id) => {
    const [categories] = await db.pool.execute(`
        SELECT c.id, c.name, c.description
        FROM tips_post tp
        JOIN categories c ON tp.category_id = c.id
        WHERE tp.id = ?
    `, [tipspost_id]);
    
    return categories.length > 0 ? categories[0] : null;
};
