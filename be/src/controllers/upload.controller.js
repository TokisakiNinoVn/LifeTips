const express = require('express');
const multer = require('multer');
const path = require('path');
const AppError = require('../utils/app-error');
const { HTTP_STATUS } = require('../constants/status-code');
const db = require('../config/db.config');

// Danh sách các định dạng ảnh và video hợp lệ
const allowedMimeTypes = [
    'image/jpeg',
    'image/png',
    'image/gif',
    'image/webp',
    'video/mp4',
    'video/quicktime',
    'video/x-msvideo',
    'video/x-matroska'
];

// Cấu hình lưu trữ multer
const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, 'public/uploads/post/');
    },
    filename: (req, file, cb) => {
        cb(null, Date.now() + path.extname(file.originalname));
    }
});

// Lọc file chỉ cho phép ảnh và video
const fileFilter = (req, file, cb) => {
    if (allowedMimeTypes.includes(file.mimetype)) {
        cb(null, true);
    } else {
        cb(new AppError(HTTP_STATUS.BAD_REQUEST, 'Invalid file type', 'Only images and videos are allowed'), false);
    }
};

// Cấu hình middleware multer
const upload = multer({ 
    storage,
    fileFilter,
    limits: { fileSize: 50 * 1024 * 1024 }
}).array('files', 10);

// Controller upload
exports.uploadPostFile = async (req, res, next) => {
    upload(req, res, async (err) => {
        try {
            if (err) {
                return next(err);
            }

            if (!req.files || req.files.length === 0) {
                return res.status(HTTP_STATUS.OK).json({
                    data: [],
                    message: 'No files uploaded'
                });
            }

            const postId = req.body.postId;
            if (!postId) {
                return next(new AppError(HTTP_STATUS.BAD_REQUEST, 'Invalid post ID', 'You must provide a valid post ID.'));
            }

            // Lưu file vào CSDL
            const files = [];
            for (const file of req.files) {
                const url = `/uploads/post/${file.filename}`;
                const type = file.mimetype.startsWith('image') ? 'image' : 'video';

                await db.pool.execute(
                    `INSERT INTO files (tippost_id, url, type_file) VALUES (?, ?, ?)`,
                    [postId, url, type]
                );

                files.push({ url, type });
            }

            return res.status(HTTP_STATUS.OK).json({
                data: files
            });
        } catch (error) {
            return next(new AppError(HTTP_STATUS.INTERNAL_SERVER_ERROR, 'Error uploading files', error.message));
        }
    });
};


exports.upload3dImage = async (req, res, next) => {
    upload(req, res, async (err) => { 
        try {
            if (!req.files || req.files.length === 0) {
                // return next(new AppError(HTTP_STATUS.BAD_REQUEST, 'No files uploaded', 'You must upload at least one file.'));
                return res.status(HTTP_STATUS.OK).json({
                    data: []
                });
            }
            const postId = req.body.postId;
            // Xử lý các file và lưu vào cơ sở dữ liệu
            const files = req.files.map(file => {
                const type = file.mimetype.startsWith('image') ? 3 : 2; // 1: Image 3d, 2: Video
                const url = `/uploads/${file.mimetype.startsWith('image') ? 'images' : 'videos'}/posts/${file.filename}`;
    
                // Thực hiện lưu vào cơ sở dữ liệu
                const insertQuery = `INSERT INTO files (id_post, url, type) VALUES (?, ?, ?)`;
                db.pool.execute(insertQuery, [postId, url, type]);
    
                return { url, type };
            });
    
            return res.status(HTTP_STATUS.OK).json({
                data: files
            });
        } catch (error) {
            return next(new AppError(HTTP_STATUS.INTERNAL_SERVER_ERROR, 'Error uploading files', error.message));
        }
    })
};