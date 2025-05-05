// user.router.js
const express = require('express');
const router = express.Router();
const { userController } = require('../../controllers/index');

router.get('/post/:userId', userController.getAllPostOfUser);
router.post('/code', userController.getCodeAccount);
router.post('/forget-password', userController.forgetPassword);

module.exports = router;
