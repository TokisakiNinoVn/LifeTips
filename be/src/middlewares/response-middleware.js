const { HTTP_STATUS } = require('../constants/status-code');

exports.format = (results, req, res, next) => {

  if (results instanceof Error) {
    return res.status(results?.statusCode || HTTP_STATUS.BAD_REQUEST).json({
      code: results?.statusCode || 400,
      status: results?.name || "failed",
      data: results?.data || [],
      message: results?.message || "Đã có lỗi gì đó mà chưa xác định được.",
    })
  }

  return res.status(HTTP_STATUS.OK).json({
    code: HTTP_STATUS.OK,
    status: "Success",
    data: results,
    message: "Thao tác thành công"
  })
}