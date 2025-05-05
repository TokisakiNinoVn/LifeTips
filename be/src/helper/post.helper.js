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

// Lấy danh mục bài viết
exports.getPostCategories = async (tipspost_id) => {
    const [categoriesOfPost] = await db.pool.execute(`
        SELECT * FROM category_post
        WHERE tipspost_id = ?
    `, [tipspost_id]);

    if (categoriesOfPost.length === 0) return [];

    const categoryIds = categoriesOfPost.map(c => c.category_id).join(',');
    const [categories] = await db.pool.execute(`
        SELECT id, name, description
        FROM categories
        WHERE id IN (${categoryIds})
    `);

    return categories.map(c => ({
        id: c.id,
        name: c.name,
        description: c.description
    }));
};
