const express = require('express');
const router = express.Router();
const { postController } = require('../../controllers/index');

router.post('/search', postController.search);
// router.post('/filter', postController.filter);
router.post('/b/same-type', postController.getPostSameCategory);

router.get('/:id', postController.getById);
router.get('/a/new', postController.getNewPost);
router.get('/c/random', postController.getRandomPost);
router.post('/d/by-user/:id', postController.getAllPostOfUser);


module.exports = router;
