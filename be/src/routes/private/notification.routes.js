// user.router.js
const express = require('express');
const router = express.Router();
const { notificationController } = require('../../controllers/index');

// Lấy tất cả thông báo của người dùng
router.get('/', notificationController.getAllNotifications);

module.exports = router;
