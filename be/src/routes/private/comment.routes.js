// user.router.js
const express = require('express');
const router = express.Router();
const { commentContrler } = require('../../controllers/index');

// Tạo bình luận
router.post('/create/:postId', commentContrler.addComment);

// Lấy tất cả bình luận của một bài đăng
router.get('/:postId', commentContrler.getAllCommentsByPostId);

module.exports = router;
