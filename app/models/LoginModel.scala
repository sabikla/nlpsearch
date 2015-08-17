package models

import play.api.db._
import anorm._
import anorm.SqlParser._
import play.api.Play.current

/**
 * @author qburst
 */
object LoginModel {
   def checkUser(username : String, password : String) : Map[Int,Int] = DB.withConnection { implicit c =>
    val userRow = SQL("SELECT userid,usertype  FROM login WHERE username= {username} AND password = {password} ").on( 
        'username -> username,
        'password -> password ).apply()
    if(userRow.isEmpty){
      Map(-1 -> -1)
    }    
    else{      
      val returnMap = Map(userRow.head [Int]("userid") -> userRow.head [Int]("usertype"))
      return returnMap
    }
  }
}