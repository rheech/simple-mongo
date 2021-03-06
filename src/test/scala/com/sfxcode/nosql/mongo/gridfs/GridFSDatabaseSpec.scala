package com.sfxcode.nosql.mongo.gridfs

import better.files.File
import com.sfxcode.nosql.mongo.gridfs.GridfsDatabase._
import com.sfxcode.nosql.mongo._
import org.bson.types.ObjectId
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll

class GridFSDatabaseSpec extends Specification with GridfsDatabaseFunctions with BeforeAll {

  "GridFSDatabase" should {

    "find file in" in {
      val fileName = "scala-logo.jpg"

      val file = findImage("filename", fileName)
      file.getFilename must be equalTo fileName

    }

    "find files by metadata value in" in {
      val fileName = "scala-logo.jpg"

      var files = findImages("group", "unknown")
      files must haveSize(0)

      files = findImages("group", "logos")
      files must haveSize(1)
    }

    "insert file and in" in {
      val fileName = "scala-logo.png"

      val oid: ObjectId = insertImage(SourcePath + fileName, ImageMetadata("template1", group = "templates"))

      var file = findImage(oid)
      file.getFilename must be equalTo fileName
      file.getMetadata.get("name").toString must be equalTo "template1"

    }

    "update metadata in" in {

      val files = findImages("group", "logos")
      files must haveSize(1)
      files.head.getMetadata.get("name").toString must be equalTo "logo2"

      // update complete metadata for one file
      updateMetadata(files.head, ImageMetadata("logo22", group = "logos"))
      // update metadata entry for all files
      updateMetadataElements(Map(), Map("group" -> "logos3", "newKey" -> "newEntryValue"))

      val file = findImage(files.head)
      file.getMetadata.get("name").toString must be equalTo "logo22"
      file.getMetadata.get("newKey").toString must be equalTo "newEntryValue"

    }

  }

  override def beforeAll(): Unit = {
    dropImages
    insertImage(SourcePath + "scala-logo.jpg", ImageMetadata("logo2", indexSet = Set(5, 6, 7)))
    imagesCount must be equalTo 1

    val file = File(TargetPath)
    if (!file.exists)
      file.createDirectory()
  }
}
