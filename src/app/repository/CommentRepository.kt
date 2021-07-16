@file:JvmName("Repository")
@file:JvmMultifileClass

package app.repository

import app.model.Comment
import java.sql.Connection

fun getCommentById(commentId: Int): Comment? {
	val SQL = "SELECT * from `CommentCounted` where `commentId`=?;"
	return connect { connection: Connection ->
		val statement = connection.prepareStatement(SQL)
		statement.setInt(1, commentId)
		statement.executeQuery()
			.tryRead<Comment>()
	}
}

/**
 * returns all comment and replies on this articleId
 */
fun listComments(articleId: Int): List<Comment> {
	val SQL = "SELECT * from `CommentCounted` where `articleId`=?; --and `reply_commentId` is null"
	return connect { connection: Connection ->
		val statement = connection.prepareStatement(SQL)
		statement.setInt(1, articleId)
		statement.executeQuery()
			.list<Comment>()
	} ?: emptyList()
}

fun deleteComment(commentId: Int): Comment? {
	val SQL = "DELETE FROM `Comment` WHERE `commentId`=? RETURNING *;"
	return connect { connection: Connection ->
		val statement = connection.prepareStatement(SQL)
		statement.setInt(1, commentId)
		statement.executeQuery()
			.tryRead<Comment>()
	}
}

/**
 * add comment to an [app.model.Article] or reply on another [app.model.Comment] (if replyCommentId>0)
 */
fun sendCommentOn(userId: Int, articleId: Int, replyCommentId: Int, content: String): Comment? {
	val SQL_C = """INSERT INTO `Comment`
			(`userId`, `content`, `time`, `notified`, `articleId`, `reply_commentId`)
			VALUES (?,?,?,0,?,null) RETURNING *;"""
	val SQL_R = """INSERT INTO `Comment`
			(`userId`, `content`, `time`, `notified`, `articleId`, `reply_commentId`)
			SELECT ?,?,?,0,`articleId`,`commentId` from `Comment` where `commentId`=? RETURNING *;"""
	return connect { connection: Connection ->
		val statement = if (replyCommentId > 0)
			connection.prepareStatement(SQL_R)
		else connection.prepareStatement(SQL_C)
		statement.setInt(1, userId)
		statement.setString(2, content)
		statement.setLong(3, System.currentTimeMillis())
		statement.setInt(4, if (replyCommentId > 0) replyCommentId else articleId)
		statement.executeQuery()
			.tryRead<Comment>()
	}
}