const express = require('express');
var app = express();

// Import child routers
const postRouter = require("./post.routes");
const uploadRouter = require("./upload.routes");
const userRouter = require("./user.routes");
const commentRouter = require("./comment.routes");
const notificationRouter = require("./notification.routes");

// Use child router
app.use("/post", postRouter);
app.use("/upload", uploadRouter);
app.use("/user", userRouter);
app.use("/comment", commentRouter);
app.use("/notification", notificationRouter);

module.exports = app;