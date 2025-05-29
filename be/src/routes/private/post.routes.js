const express = require('express');
const router = express.Router();
const { postController } = require('../../controllers/index');
const { uploadWithCatch } = require('../../middlewares/multer.middleware');

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
// Tìm kiếm bài đăng
router.post('/e/search', postController.search);

// Tạo bài đăng
router.post('/create-with-file', uploadWithCatch, postController.createPostWithFile);
// Lưu bài đăng
router.post('/save', postController.savePost);

// Cập nhật bài đăng
router.put('/update/:id', postController.update);

// Xóa bài đăng
router.delete('/delete/:id', postController.delete);
// Xóa bài đăng đã lưu của người dùng
router.delete('/a/saved/:postId', postController.deleteSavedPost);



module.exports = router; 
