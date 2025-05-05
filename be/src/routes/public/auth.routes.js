const express = require('express');
const router = express.Router();
const { authentication } = require('../../controllers/index');

router.post('/login',  authentication.login);
router.post('/register',  authentication.register);

module.exports = router;
