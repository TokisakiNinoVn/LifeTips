// user.router.js
const express = require('express');
const router = express.Router();
const { userController } = require('../../controllers/index');

// // Route to get all users
// router.get('/', userController.getAllUsers);
// // Route to get all basic users
// router.get('/basic', userController.getAllUsersBasic);

// // Route to get a user by ID
// router.get('/:id', userController.getUserById);

// // Route to update a user's information
router.put('/:id', userController.updateUser);

// // Route to delete a user
router.delete('/:id', userController.deleteUser);

router.post('/save', userController.savePost);
router.post('/unsave', userController.unSavePost);

router.get('/saved/:userId', userController.getSavedPost);
router.get('/post/:userId', userController.getAllPostOfUser);
router.post('/password', userController.changePassword);

module.exports = router;
