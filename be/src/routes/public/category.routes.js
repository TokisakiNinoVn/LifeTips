const express = require('express');
const router = express.Router();
const { categoryController } = require('../../controllers/index');

router.get('/all', categoryController.getAllCategory);

module.exports = router;