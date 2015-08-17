package controllers

import java.security.MessageDigest
import scala.io.Source
import scala.util.control.Breaks

/**
 * @author qburst
 */
object GeneralFunctions {
  
  val fileRoot = "public/corpus/"
  val job = Source.fromFile(fileRoot.toString()+"jobs").getLines().toList //List("software engineer","senior software engineer","software architect","receptionist","sweeper","software")
  val place = Source.fromFile(fileRoot.toString()+"locations").getLines().toList //List("calicut", "cochin", "banglore", "chennai", "trivandrum")
  val skills = Source.fromFile(fileRoot.toString()+"skills").getLines().toList //List("java", "scala", "python", "javascript","ios","android")
  val country = Source.fromFile(fileRoot.toString()+"country").getLines().toList
  val states = Source.fromFile(fileRoot.toString()+"states").getLines().toList
  val andConnector = List("in","at")
  val loop = new Breaks
  
  def getMD5Hash(input : String ) : String = {
    return MessageDigest.getInstance("MD5").digest(input.getBytes).map("%02x".format(_)).mkString
  }
  
  def someExtractor(obj:Any) : String = {
    val returnValue = obj match {
      case Some(value) => value 
      case value => value
    } 
    returnValue.toString()
  }
  
  def getLocationStringFromMap(jobData : List[Map[String,Any]]) : String = {
    var jobLocation : String = ""
    jobData.map( job =>{
        val individualLocation = GeneralFunctions.someExtractor(job.get("joblocation.location").get)
        if(!jobLocation.contains(individualLocation.capitalize)){
          if(!jobLocation.isEmpty()){
            jobLocation += ","
          }
          jobLocation += individualLocation.capitalize 
        }
      }
    ) 
    jobLocation
  }
  
  def getSkillStringFromMap(jobData : List[Map[String,Any]]) : String = {
    var jobSkill : String = ""
    jobData.map( job =>{
        val individualSkill = GeneralFunctions.someExtractor(job.get("jobskill.skill").get)
        if(!jobSkill.contains(individualSkill.capitalize)){
          if(!jobSkill.isEmpty()){
            jobSkill += ","
          }
          jobSkill += individualSkill.capitalize 
        }
      }
    ) 
    jobSkill
  }
  
  
  
  def searchQueryGenerator(userQuery : String) : String = {      
    val inputList = userQuery.split(" and | or ")
    
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
    qry += checkExperienceConstraint(userQuery)    
    qry += checkSalaryConstraint(userQuery)
    qry = qry.stripPrefix(" AND ") .stripSuffix(" AND ").trim()
    
    var actualQuery ="""SELECT A.jobid,A.post,A.company,A.description,A.salary,A.experience,L.location,S.skill FROM jobtable A 
          INNER JOIN joblocation L on L.jobid=A.jobid
          INNER JOIN jobskill S on S.jobid=A.jobid"""
    
    if(!qry.isEmpty()){
      actualQuery += " WHERE " +qry
    }
    println(actualQuery)
    actualQuery
  }
  
  def constraintConstructor(userQuery : String) : String={
    var joblist : List[String] = List()
    var placelist : List[String] = List()
    var skillslist : List[String] = List()
    var countrylist : List[String] = List()
    var statelist : List[String] = List()
    var qry : String =""
    
    /*Checking the job designation begins*/
    loop.breakable{
      var inputQuery = userQuery
      for( i <- 3 to 1 by -1){
        val gramList = inputQuery.split(' ').sliding(i).map( p => p.mkString(" ") ).toList
        joblist  = joblist ::: job.filter (x => (gramList.contains(x)))
        if(!joblist.isEmpty){
          //This eliminates further checking for the lower gramlist :-)
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
         //This eliminates further checking for the lower gramlist :-)
          for(matches <- placelist){
            inputQuery = inputQuery.split(matches).mkString("")
          }
        }
      }
    }
    
    /*Checking for the country*/
    loop.breakable{
      var inputQuery = userQuery
      for( i <- 3 to 1 by -1){
        val gramList = inputQuery.split(' ').sliding(i).map( p => p.mkString(" ") ).toList
        countrylist  = countrylist ::: country.filter (x => (gramList.contains(x)))
        if(!countrylist.isEmpty){
          //This eliminates further checking for the lower gramlist :-)
          for(matches <- countrylist){
            inputQuery = inputQuery.split(matches).mkString("")
          }
        }
      }
    }
    
    /*Checking for the states*/
    loop.breakable{
      var inputQuery = userQuery
      for( i <- 3 to 1 by -1){
        val gramList = inputQuery.split(' ').sliding(i).map( p => p.mkString(" ") ).toList
        statelist  = statelist ::: states.filter (x => (gramList.contains(x)))
        if(!statelist.isEmpty){
          //This eliminates further checking for the lower gramlist :-)
          for(matches <- statelist){
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
          //This eliminates further checking for the lower gramlist :-)
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
        qry += """ AND"""
      }        
      qry = qry + " skill='"+ skill+"'"
    }
    
    for(countryItem <- countrylist){
      if(qry.contains("location=")||qry.contains("skill=")||qry.contains("post=")||qry.contains("country=")){
        qry += """ AND"""
      }        
      qry = qry + " country='"+ countryItem+"'"
    }
    
    for(stateItem <- statelist){
      if(qry.contains("location=")||qry.contains("skill=")||qry.contains("post=")||qry.contains("country=")||qry.contains("state=")){
        qry += """ AND"""
      }        
      qry = qry + " state='"+ stateItem+"'"
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
  
  def isNumeric(input: String): Boolean = input.forall(_.isDigit)
}