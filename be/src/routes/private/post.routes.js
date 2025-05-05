const express = require('express');
const router = express.Router();
const { postController } = require('../../controllers/index');

// Tạo bài đăng
router.post('/create', postController.create);

// Cập nhật bài đăng
router.put('/update/:id', postController.update);

// Xóa bài đăng
router.delete('/delete/:id', postController.delete);

// Lấy thông tin chi tiết bài đăng
router.get('/:id', postController.getById);

// Lấy tất cả bài đăng của một người dùng
// router.get('/user/:userId', postController.getAllPostOfUser);

// Tìm kiếm bài đăng theo địa chỉ
// router.post('/search', postController.searchByAddress);

// Lọc bài đăng theo tiêu chí
// router.post('/filter', postController.filter);

// Lưu bài đăng
// router.post('/save', postController.savePost);

// Lấy danh sách bài đăng đã lưu
// router.get('/saved/:userId', postController.getSavedPost);

// Lấy bài đăng mới nhất
// router.get('/new', postController.getNewPost);

// Lấy bài đăng cùng loại
// router.post('/same-type', postController.getPostSameType);

module.exports = router; 
