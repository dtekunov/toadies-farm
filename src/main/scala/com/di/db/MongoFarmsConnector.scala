package com.di.db

import com.di.domain.{DeadToad, Farm, GrownToad, Owner, Tadpole, Toad}
import com.di.jsonFormatters.FormatDoc
import com.mongodb.client.result.DeleteResult
import com.typesafe.config.Config
import org.mongodb.scala.{Completed, Document, MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Filters.{and, equal}
import org.mongodb.scala.model.Updates.set
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import org.mongodb.scala._

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class MongoFarmsConnector(url: String, dbName: String)(implicit ec: ExecutionContext) {

  private val mongoClient: MongoClient = MongoClient(url)

  private val database: MongoDatabase =
    mongoClient.getDatabase(dbName)

  private val farmsCollection: MongoCollection[Document] =
    database.getCollection("farms")

  private val grownToadsCollection: MongoCollection[Document] =
    database.getCollection("grown_toads")

  private val tadPolesCollection: MongoCollection[Document] =
    database.getCollection("tadpoles")

  private val deadToadsCollection: MongoCollection[Document] =
    database.getCollection("dead_toads")

  private val ownerCollection: MongoCollection[Document] =
    database.getCollection("owner")

  def insertSingleFarm(farm: Farm): Future[Option[Completed]] = {
    val docToInsert = Document(
      Farm.idDbName -> farm.id,
      Farm.nameDbName -> farm.name,
      Farm.modeDbName -> farm.mode,
      Farm.isCannibalDbName -> farm.isCannibal)

    farmsCollection.insertOne(docToInsert).toFutureOption()
  }

  def insertSingleOwner(owner: Owner): Future[Option[Completed]] = {
    val ownerToInsert = Document(
      Owner.idDb -> owner.id,
      Owner.farmNameDb -> owner.farmName,
      Owner.balanceDb -> owner.balance,
      Owner.isCreativeDb -> owner.isCreative,
      Owner.transactionsMadeDb -> owner.transactionsMade,
      Owner.numberOfCyclesDb -> owner.numberOfCycles.toString())

    ownerCollection.insertOne(ownerToInsert).toFutureOption()
  }

  def updateFarmName(oldName: String, newName: String): Future[Option[UpdateResult]] =
    farmsCollection.updateOne(
      equal(Farm.nameDbName, oldName),
      set(Farm.nameDbName, newName)
    ).toFutureOption()

  def getAllFarms: Future[Seq[Document]] =
    farmsCollection.find().toFuture()

  def getAllOwners: Future[Seq[Document]] =
    ownerCollection.find().toFuture()

  def getAllToadsFromDb: Future[Seq[Document]] = for {
    grownToads <- grownToadsCollection.find().toFuture()
    tadPoles <- tadPolesCollection.find().toFuture()
    deadToads <- deadToadsCollection.find().toFuture()
  } yield {
    grownToads ++ tadPoles ++ deadToads
  }

  def getAllToadsByFarm(farmName: String): Future[Vector[Toad]] = {
    for {
      grownToads <- getAllGrownToadsByFarm(farmName)
      tadPoles <- getAllTadPolesByFarm(farmName)
      deadToads <- getAllDeadToadsByFarm(farmName)
    } yield {
      val toads: Seq[Toad] =
          grownToads.map(doc => FormatDoc.toGrownToad(doc)) ++
          tadPoles.map(doc => FormatDoc.toTadpole(doc)) ++
          deadToads.map(doc => FormatDoc.toDeadToad(doc))

      toads.toVector
    }
  }

  private def getAllGrownToadsByFarm(farmName: String): Future[Seq[Document]] =
    grownToadsCollection.find(equal(GrownToad.farmNameDb, farmName)).toFuture()

  private def getAllTadPolesByFarm(farmName: String): Future[Seq[Document]] =
    tadPolesCollection.find(equal(Tadpole.farmNameDb, farmName)).toFuture()

  private def getAllDeadToadsByFarm(farmName: String): Future[Seq[Document]] =
    deadToadsCollection.find(equal(DeadToad.farmNameDb, farmName)).toFuture()

  def getFarmByName(name: String): Future[Option[Document]] =
    farmsCollection.find(equal(Farm.nameDbName, name)).first().toFutureOption()

  def getOwnerByFarmName(farmName: String): Future[Option[Document]] =
    ownerCollection.find(equal(Owner.farmNameDb, farmName)).first().toFutureOption()

//  def getHeadLog: Future[Option[Document]] =
//    userLogsCollection.find().first().toFutureOption()
//
//  def getEntryByAuth(toFind: String): Future[Option[Document]] =
//    entriesCollection.find(equal(Entries.authEntryDbFieldName, toFind)).first().toFutureOption()
//
//  def getEntryByHostname(toFind: String): Future[Option[Document]] =
//    entriesCollection.find(equal(Entries.hostnameDbFieldName, toFind)).first().toFutureOption()
//
//  def getEntryByAuthAndHostname(auth: String, hostname: String): Future[Option[Document]] =
//    entriesCollection.find(and(
//      equal(Entries.authEntryDbFieldName, auth),
//      equal(Entries.hostnameDbFieldName, hostname))
//    ).first().toFutureOption()
//
//  def deleteByAuth(auth: String): Future[Option[DeleteResult]] =
//    entriesCollection.deleteOne(equal(Entries.authEntryDbFieldName, auth)).toFutureOption()

  case class DeletionResult(toadsDeleted: Long)

  def deleteFarmByName(name: String): Future[DeletionResult] = for {
    _ <- deleteOwnerByFarmName(name)
    deleteToads <- deleteToadsInAFarm(name)
    _ <- farmsCollection.deleteOne(equal(Farm.nameDbName, name)).toFutureOption()
  } yield {
      DeletionResult(deleteToads)
  }

  private def deleteOwnerByFarmName(farmName: String): Future[Option[DeleteResult]] =
    ownerCollection.deleteOne(equal(Owner.farmNameDb, farmName)).toFutureOption()

  private def deleteToadsInAFarm(farmName: String): Future[Long] = {
    for {
      delete1 <- grownToadsCollection.deleteMany(equal(GrownToad.farmNameDb, farmName)).toFutureOption()
      delete2 <- deadToadsCollection.deleteMany(equal(DeadToad.farmNameDb, farmName)).toFutureOption()
      delete3 <- tadPolesCollection.deleteMany(equal(Tadpole.farmNameDb, farmName)).toFutureOption()
    } yield {
      Seq(delete1, delete2, delete3).foldLeft(0L) { (acc, elem) =>
        if (elem.nonEmpty) acc + elem.get.getDeletedCount
        else acc
      }
    }
  }

//
//  def deleteByHostname(hostnameToDelete: String): Future[Option[DeleteResult]] = {
//    entriesCollection.deleteOne(equal(Entries.hostnameDbFieldName, hostnameToDelete)).toFutureOption()
//  }
}

object MongoFarmsConnector {
  def initiateDb(config: Config)(implicit ec: ExecutionContext): MongoFarmsConnector = {
    val url = config.getString("main.db.url")
    val dbName = config.getString("main.db.name")
    new MongoFarmsConnector(url, dbName)
  }
}