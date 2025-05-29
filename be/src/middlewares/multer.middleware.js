// middleware/multer.js
const multer = require('multer');
const path = require('path');

// Cấu hình storage
const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        try {
            cb(null, 'public/uploads/');
        } catch (err) {
            console.error('Error setting destination:', err);
            cb(err);
        }
    },
    filename: function (req, file, cb) {
        try {
            cb(null, Date.now() + '_' + file.originalname);
        } catch (err) {
            console.error('Error setting filename:', err);
            cb(err);
        }
    }
});



// Middleware xử lý lỗi upload
const uploadWithCatch = (req, res, next) => {
    const uploader = upload.array('files'); // hoặc .array(), .fields() tùy use-case

    uploader(req, res, function (err) {
        if (err instanceof multer.MulterError) {
            // Lỗi liên quan đến multer (dung lượng, định dạng, ...)
            return res.status(400).json({ error: 'Multer error', message: err.message });
        } else if (err) {
            // Lỗi khác (vd: lỗi hệ thống)
            return res.status(500).json({ error: 'Unknown upload error', message: err.message });
        }
        next();
    });
};
const upload = multer({ storage: storage });

module.exports = {
    upload,
    uploadWithCatch
};
