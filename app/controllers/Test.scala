package controllers



import play.api._
import play.api.mvc._
import models.ApplicationModel
import models.LoginModel
import scala.io.Source
import scala.util.control.Breaks

/**
 * @author qburst
 */
class Test extends Controller {
  
  val fileRoot = "public/corpus/"
  val job = Source.fromFile(fileRoot.toString()+"jobs").getLines().toList //List("software engineer","senior software engineer","software architect","receptionist","sweeper","software")
  val place = Source.fromFile(fileRoot.toString()+"locations").getLines().toList //List("calicut", "cochin", "banglore", "chennai", "trivandrum")
  val skills = Source.fromFile(fileRoot.toString()+"skills").getLines().toList //List("java", "scala", "python", "javascript","ios","android")
  val andConnector = List("in","at")
  val loop = new Breaks
    
  def testSearch = Action { implicit request =>
    val input = request.body.asFormUrlEncoded.get.get("searchQuery").get.head
    val inputList = input.split(" and | or ")
    
    var qry : String = ""
    for(split <- inputList){      
      val constraintSplit ="\n("+constraintConstructor(split)+") OR "
      if(constraintSplit.length()>7){        
    	  qry += constraintSplit
      }
    }
    qry = qry.stripSuffix(" OR ").trim()
    if(!qry.isEmpty()/*&&qry.length()>2*/){
      qry = "("+qry+")"
    }
    qry += checkExperienceConstraint(input)
    
    qry += checkSalaryConstraint(input)
    qry = qry.stripPrefix(" AND ") .stripSuffix(" AND ").trim()
    Ok(qry)
  }
  
  
  def constraintConstructor(userQuery : String) : String={
    var joblist : List[String] = List()
    var placelist : List[String] = List()
    var skillslist : List[String] = List()
    var qry : String =""
    
    /*Checking the job designation begins*/
    loop.breakable{
      var inputQuery = userQuery
      for( i <- 3 to 1 by -1){
        val gramList = inputQuery.split(' ').sliding(i).map( p => p.mkString(" ") ).toList
        joblist  = joblist ::: job.filter (x => (gramList.contains(x)))
        if(!joblist.isEmpty){
          for(matches <- joblist){
            inputQuery = inputQuery.split(matches).mkString("")
          }
        }
      }
    }
    
    /*Checking for the locations*/
    loop.breakable{
      var inputQuery = userQuery
      for( i <- 3 to 1 by -1){
        val gramList = inputQuery.split(' ').sliding(i).map( p => p.mkString(" ") ).toList
        placelist  = placelist ::: place.filter (x => (gramList.contains(x)))
        if(!placelist.isEmpty){
          for(matches <- placelist){
            inputQuery = inputQuery.split(matches).mkString("")
          }
        }
      }
    }
    
    /*checking for skills match*/
    loop.breakable{
      var inputQuery = userQuery
      for( i <- 3 to 1 by -1){
        val gramList = inputQuery.split(' ').sliding(i).map( p => p.mkString(" ") ).toList
        skillslist  = skillslist ::: skills.filter (x => (gramList.contains(x)))
        if(!skillslist.isEmpty){
          for(matches <- skillslist){
            inputQuery = inputQuery.split(matches).mkString("")
          }
        }
      }
    }
    
    var inputQuery = userQuery
    for(job <- joblist){  
       if(qry.contains("post=")){
         qry += """ OR"""
       }
      qry = qry + " post='"+job+"'"
    }
    
    inputQuery = userQuery
    for(place <- placelist){
      val firstPlace = placelist.head
      val contraintQry = inputQuery.substring(0, inputQuery.indexOf(firstPlace))
      
      if(qry.contains("location=")||qry.contains("post=")){
    	  if(contraintQry.matches(".*in.*|.*at.*")){
    		  qry += """ AND"""
    	  }
        else{          
        	qry += """ OR"""
        }
       }
      qry = qry + " location='"+place+"'" 
    }
    
    for(skill <- skillslist){
      if(qry.contains("location=")||qry.contains("skill=")||qry.contains("post=")){
        qry += """ OR"""
      }        
      qry = qry + " skill='"+ skill+"'"
    }
        
    qry
  }
  
  def checkExperienceConstraint(userQuery : String) : String ={
    var qry =""
    if(userQuery.contains("experience")||userQuery.contains("fresher")){ 
      
      var endIndex = userQuery.length()
      val begIndex = userQuery.indexOf("experience")
      if(userQuery.contains("experience")&&(begIndex < userQuery.indexOf("salary"))){
        endIndex = userQuery.indexOf("salary")
      }
      
      var experienceYear = 0
      if(userQuery.contains("experience")){        
        var inputQuery = userQuery.substring(begIndex,endIndex)
        val tokens = inputQuery.split("\\s+")
        loop.breakable{
          tokens.foreach { x =>
            if(GeneralFunctions.isNumeric(x)){
              experienceYear = x.toString().toInt
              loop.break()
            }          
          }
        }
        if(inputQuery.contains("greater")||inputQuery.contains(">")||inputQuery.contains("more")||inputQuery.contains("higher")||inputQuery.contains("above")){
          qry += " experience>"+experienceYear
        }
        else if(inputQuery.contains("less")||inputQuery.contains("<")||inputQuery.contains("fewer")||inputQuery.contains("lower")||inputQuery.contains("below")){
          qry += " experience<"+experienceYear
        }
        else{
          qry += " experience="+experienceYear
        }
      }
      else{
        qry += " experience=0"
      }
    }
    if(!qry.isEmpty()){
      qry = " AND " + qry
    }
    qry
  }
  
  def checkSalaryConstraint(userQuery : String) : String ={
    var qry = ""
     if(userQuery.contains("salary")){

      var endIndex = userQuery.length()
      val begIndex = userQuery.indexOf("salary")
      if(userQuery.contains("experience")&&(begIndex < userQuery.indexOf("experience"))){
        endIndex = userQuery.indexOf("experience")
      }
      
      
      var inputQuery = userQuery.substring(begIndex,endIndex)
      var salaryAmount = 0
      val tokens = inputQuery.split("\\s+")
      loop.breakable{
        tokens.foreach { x =>
          if(GeneralFunctions.isNumeric(x)){
            salaryAmount = x.toString().toInt
            loop.break()
          }          
        }
      }
      
      if(inputQuery.contains("greater")||inputQuery.contains(">")||inputQuery.contains("more")||inputQuery.contains("higher")||inputQuery.contains("above")){
        qry += " salary>"+salaryAmount
      }
      else if(inputQuery.contains("less")||inputQuery.contains("<")||inputQuery.contains("fewer")||inputQuery.contains("lower")||inputQuery.contains("below")){
        qry += " salary<"+salaryAmount
      }
      else{
        qry += " salary="+salaryAmount
      }
    }
    
    if(!qry.isEmpty()){
      qry = " AND " + qry
    }
    qry
  }
  

}




/*

def OLD_SEARCH_QUERY_GENERATOR(userQuery: String):String ={
  //    val job = List("Software Engineer","Senior Software Engineer","Software Architect","receptionist","sweeper")
    val fileRoot = "public/corpus/"
    val job = Source.fromFile(fileRoot.toString()+"jobs").getLines().toList //List("software engineer","senior software engineer","software architect","receptionist","sweeper","software")
    val place = Source.fromFile(fileRoot.toString()+"locations").getLines().toList //List("calicut", "cochin", "banglore", "chennai", "trivandrum")
    val skills = Source.fromFile(fileRoot.toString()+"skills").getLines().toList //List("java", "scala", "python", "javascript","ios","android")
    
    var joblist : List[String] = List()
    var placelist : List[String] = List()
    var skillslist : List[String] = List()
    val tokens = userQuery.toLowerCase().split("\\s+")
    
    Checking the job designation begins
    val loop = new Breaks
    loop.breakable{
      var inputQuery = userQuery
      for( i <- 3 to 1 by -1){
        val gramList = inputQuery.split(' ').sliding(i).map( p => p.mkString(" ") ).toList
        joblist  = joblist ::: job.filter (x => (gramList.contains(x)))
        if(!joblist.isEmpty){
          for(matches <- joblist){
            inputQuery = inputQuery.split(matches).mkString("")
          }
        }
      }
    }
    
    Checking for the locations
    loop.breakable{
      var inputQuery = userQuery
      for( i <- 3 to 1 by -1){
        val gramList = inputQuery.split(' ').sliding(i).map( p => p.mkString(" ") ).toList
        placelist  = placelist ::: place.filter (x => (gramList.contains(x)))
//        println(placelist)
        if(!placelist.isEmpty){
          for(matches <- placelist){
            inputQuery = inputQuery.split(matches).mkString("")
          }
        }
      }
    }
    
    checking for skills match
    loop.breakable{
      var inputQuery = userQuery
      for( i <- 3 to 1 by -1){
        val gramList = inputQuery.split(' ').sliding(i).map( p => p.mkString(" ") ).toList
        skillslist  = skillslist ::: skills.filter (x => (gramList.contains(x)))
        if(!skillslist.isEmpty){
          for(matches <- skillslist){
            inputQuery = inputQuery.split(matches).mkString("")
          }
        }
      }
    }
    var qry = """SELECT A.jobid,A.post,A.company,A.description,A.salary,A.experience,L.location,S.skill FROM jobtable A 
          INNER JOIN joblocation L on L.jobid=A.jobid
          INNER JOIN jobskill S on S.jobid=A.jobid"""
    if(!joblist.isEmpty){
      if(!qry.contains("WHERE")){
        qry += """
          WHERE
        """
      }
      for(job <- joblist){  
         if(qry.contains("post=")){
           qry += """ OR"""
         }
        qry = qry + " post='"+job+"'"
      }
    }
    if(!placelist.isEmpty){
       if(!qry.contains("WHERE")){
        qry += """
          WHERE
        """
       }
       for(place <- placelist){
         if(placelist.size>1&&placelist.indexOf(place)>0){
          val beginIndex = userQuery.indexOf(placelist.apply(placelist.indexOf(place)-1))
          val endIndex = userQuery.indexOf(placelist.apply(placelist.indexOf(place)))
          val inputQuery = userQuery.substring(beginIndex, endIndex)
          if(inputQuery.contains("and")){
            qry += """ AND"""
          }
          else if(inputQuery.contains("or")){
            qry += """ OR"""
          }
          println("\n\n\n\n"+inputQuery)
        }
        else{
          if(qry.contains("post=")){            
            val beginIndex = userQuery.indexOf(joblist.reverse.head)
            val endIndex = userQuery.indexOf(placelist.apply(placelist.indexOf(job)))
            val inputQuery = userQuery.substring(beginIndex, endIndex)
            if(inputQuery.contains("and")){
              qry += """ AND"""
            }
            else if(inputQuery.contains("or")){
              qry += """ OR"""
            }
          }
        }
        if(qry.contains("location=")||qry.contains("post=")){
           qry += """ OR"""
         }
        qry = qry + " location='"+place+"'" 
       }
    }
    if(!skillslist.isEmpty){
       if(!qry.contains("WHERE")){
        qry += """
          WHERE
        """
      }
      for(skill <- skillslist){
        if(qry.contains("location=")||qry.contains("skill=")||qry.contains("post=")){
          qry += """ OR"""
        }        
        qry = qry + " skill='"+ skill+"'"
      }
    }
    if(userQuery.contains("salary")){
      if(!qry.contains("WHERE")){
        qry += """
          WHERE
        """
      }
      if(qry.contains("location=")||qry.contains("skill=")||qry.contains("post=")){
          qry += """ OR"""
      }
      var inputQuery = userQuery.substring(userQuery.indexOf("salary"))
      var salaryAmount = 0
      val tokens = inputQuery.split("\\s+")
      tokens.foreach { x =>
        if(isNumeric(x)){
          salaryAmount = x.toString().toInt 
        }          
      }
      if(inputQuery.contains("greater")||inputQuery.contains(">")||inputQuery.contains("more")||inputQuery.contains("higher")){
        qry += " salary>"+salaryAmount
      }
      else if(inputQuery.contains("less")||inputQuery.contains("<")||inputQuery.contains("fewer")||inputQuery.contains("lower")){
        qry += " salary<"+salaryAmount
      }
      else{
        qry += " salary="+salaryAmount
      }
    }
    
    if(userQuery.contains("experience")||userQuery.contains("fresher")){
      if(!qry.contains("WHERE")){
        qry += """
          WHERE
        """
      }
      if(qry.contains("location=")||qry.contains("skill=")||qry.contains("post=")){
          qry += """ OR"""
      }
      var experienceYear = 0
      if(userQuery.contains("experience")){        
        var inputQuery = userQuery.substring(userQuery.indexOf("experience"))
            val tokens = inputQuery.split("\\s+")
            tokens.foreach { x =>
            if(isNumeric(x)){
              experienceYear = x.toString().toInt 
            }          
        }
        if(inputQuery.contains("greater")||inputQuery.contains(">")||inputQuery.contains("more")||inputQuery.contains("higher")||inputQuery.contains("above")){
          qry += " experience>"+experienceYear
        }
        else if(inputQuery.contains("less")||inputQuery.contains("<")||inputQuery.contains("fewer")||inputQuery.contains("lower")||inputQuery.contains("below")){
          qry += " experience<"+experienceYear
        }
        else{
          qry += " experience="+experienceYear
        }
      }
      else{
        qry += " experience=0"
      }
    }
    println(qry)
    qry    
}*/ 