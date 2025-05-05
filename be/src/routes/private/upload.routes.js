// app.js or server.js

const express = require('express');
const router = express.Router();
const { uploadController } = require('../../controllers/index'); 

router.post('/', uploadController.uploadPostFile);
// router.post('/3d', uploadController.upload3dImage);

module.exports = router;
