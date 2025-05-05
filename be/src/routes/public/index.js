const express = require('express');
const router = express.Router();

// Import child routers
const authRouter = require('./auth.routes');
const postRouter = require('./post.routes');
const categoryRouter = require('./category.routes');
const userRouter = require('./user.routes');

// Use child router
router.use('/auth', authRouter);
router.use('/post', postRouter);
router.use('/category', categoryRouter);
router.use('/user', userRouter);

module.exports = router;
