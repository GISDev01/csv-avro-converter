package me.jairam

import java.io.File

import me.jairam.avro.AvroWriter
import me.jairam.csv.CsvReader
import me.jairam.schema.Builder
import org.rogach.scallop.ScallopConf

class CsvAvroConverter(args: Seq[String]) extends ScallopConf(args) {
  val in = opt[File](required = true, descr = "Input CSV")
  val out = opt[String](required = true, descr = "Output file name")

  verify()
}

object CsvAvroConverter extends App {

  val cli = new CsvAvroConverter(args)
  val input = cli.in()
  val output = new File(cli.out())

  val csvReader = new CsvReader(input)
  val avroWriter = new AvroWriter(output)

  for {
    rows <- csvReader.rows()
    schema <- csvReader.inferSchema()
  } {
    val avroSchema = Builder.buildSchema(schema, input.getName, input.getParent)
    avroWriter.write(rows, avroSchema)
  }

  println(s"${output.getAbsolutePath} created")
}
