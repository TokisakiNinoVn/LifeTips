const express = require('express');
const router = express.Router();
// const { uploadWithCatch } = require('../../middlewares/multer.middleware');
const { postController } = require('../../controllers/index');

// Lấy thông tin chi tiết bài đăng
router.get('/:id', postController.getById);
// Lấy bài đăng ngẫu nhiên
router.get('/a/random', postController.getRandomPost);
// Lấy bài đăng cùng loại
router.get('/b/same-type/:categoryId', postController.getPostSameCategory);
// Lấy danh sách bài đăng của người dùng
router.get('/c/user', postController.getAllPostOfUser);
// Lấy danh sách bài đăng đã lưu của người dùng
router.get('/d/saved', postController.getSavedPost);
// Lấy danh sách bài đăng đã lưu của người dùng
router.get('/f/all', postController.getAllPosts);

// Tìm kiếm bài đăng
router.post('/e/search', postController.search);


module.exports = router;
