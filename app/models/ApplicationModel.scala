package models



import anorm._
import play.api.db._
import anorm.SqlParser._
import play.api.Play.current
import org.postgresql.util.PSQLException
import play.Logger

/**
 * @author qburst
 */
object ApplicationModel  {
  
  def readJobByQuery(query : String) : List[Map[String,Any]] =  DB.withConnection { implicit c =>
    val sql = SQL(query)    
   
    val result = sql().map(row =>      
      row.asMap
    ).toList
    return result; 
  }  
  
  def readLatestFiveJobs(): List[Map[String,Any]] =  DB.withConnection { implicit c =>
    val sql = SQL("""SELECT * FROM jobtable ORDER BY jobid DESC LIMIT 6""")    
   
    val result = sql().map(row =>      
      row.asMap
    ).toList
    return result; 
  }  
  
  def readLocationOfJob(jobid : Int) :  List[Map[String,Any]]= DB.withConnection { implicit c =>
    val sql = SQL("SELECT * FROM joblocation WHERE jobid={jobid}").on(
      'jobid -> jobid  
    )          
    val result = sql().map(row =>      
      row.asMap
    ).toList
    return result; 
  }
  
  
  def readSkillOfJob(jobid : Int) :  List[Map[String,Any]]= DB.withConnection { implicit c =>
    val sql = SQL("SELECT * FROM jobskill WHERE jobid={jobid}").on(
      'jobid -> jobid  
    )          
    val result = sql().map(row =>      
      row.asMap
    ).toList
    return result; 
  }
  
  def saveFeedback(name : String, email : String, mobile : String, subject : String, message : String) : Int = DB.withConnection { implicit c =>
    try{
      val result = SQL("INSERT INTO feedback (name,email,mobile,subject,message) VALUES({name},{email},{mobile},{subject},{message})").on(
          'name -> name,
          'email  -> email,
          'mobile  -> mobile,
          'subject  -> subject,
          'message  -> message
          ).executeUpdate()
          result   
    }
    catch{
      case ex : PSQLException => {
        Logger.debug("saveFeedback in ApplicationModel : "+ex.getMessage)
        return 0
      }
    }
  }
  
  def saveCandidate(name : String, email : String, mobile : String, password : String, location : String, experience : Int, resume : String) : Int = DB.withConnection { implicit c =>
    try{
      val userid = saveNewUser(email, password, 2)
      val result = SQL("INSERT INTO candidate(userid, name, email, mobile, location, experience, resume) VALUES({userid}, {name}, {email}, {mobile}, {location}, {experience}, {resume})").on(
          'userid -> userid, 
          'name -> name, 
          'email -> email, 
          'mobile -> mobile, 
          'location -> location, 
          'experience -> experience, 
          'resume -> resume
          ).executeUpdate()
          result   
    }
    catch{
      case ex : PSQLException => {
        Logger.debug("saveCandidate in ApplicationModel : "+ex.getMessage)
        return 0
      }
    }
  }
  
  
  
  def saveCompany(name : String, email : String, mobile : String, password : String, address : String) : Int = DB.withConnection { implicit c =>
    try{
      val userid = saveNewUser(email, password, 3)
      val result = SQL("INSERT INTO company(userid, name, email, mobile, address) VALUES({userid}, {name}, {email}, {mobile}, {address})").on(
          'userid -> userid, 
          'name -> name, 
          'email -> email, 
          'mobile -> mobile, 
          'address -> address
          ).executeUpdate()
          result   
    }
    catch{
      case ex : PSQLException => {
        Logger.debug("saveCompany in ApplicationModel : "+ex.getMessage)
        return 0
      }
    }
  }
  
  def saveNewUser(username : String, password : String, userType : Int) : Int = DB.withConnection{ implicit c =>
    try{
      var userId : Int = 0
      val result = SQL("INSERT INTO login (username, password, userType) VALUES({username}, {password}, {userType})").on(
          'username -> username, 
          'password -> password, 
          'userType -> userType
          ).executeUpdate()
      if(result > 0){
        val qry = SQL("SELECT max(userid) as userid FROM login")
        qry().map { row =>          
          userId =(row.asMap.get(".userid").get).toString().toInt
        }
      }  
      userId
    }
    catch{
      case ex : PSQLException => {
        Logger.debug("saveFeedback in ApplicationModel : "+ex.getMessage)
        return 0
      }
    }
  }
  
}