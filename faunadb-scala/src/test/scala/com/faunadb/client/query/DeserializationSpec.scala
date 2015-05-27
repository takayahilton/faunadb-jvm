package com.faunadb.client.query

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.scalatest.{Matchers, FlatSpec}

class DeserializationSpec extends FlatSpec with Matchers {
  val json = new ObjectMapper()
  json.registerModule(new DefaultScalaModule)
  json.registerModule(new SimpleModule().setDeserializerModifier(new FaunaDeserializerModifier))

  "Query AST deserialization" should "deserialize a query response with refs" in {
    val toDeserialize = "{\n\t\t\"ref\": {\n\t\t\t\"@ref\": \"classes/spells/93044099947429888\"\n\t\t},\n\t\t\"class\": {\n\t\t\t\"@ref\": \"classes/spells\"\n\t\t},\n\t\t\"ts\": 1424992618413105,\n\t\t\"data\": {\n\t\t\t\"refField\": {\n\t\t\t\t\"@ref\": \"classes/spells/93044099909681152\"\n\t\t\t}\n\t\t}\n\t}"
    val parsed = json.readValue[Instance](toDeserialize, classOf[Instance])
    parsed shouldBe Instance(Ref("classes/spells/93044099947429888"), Ref("classes/spells"), 1424992618413105L, ObjectV("refField" -> Ref("classes/spells/93044099909681152")))
  }

  it should "deserialize a query response" in {
    import Values._
    val toDeserialize = "{\n        \"class\": {\n            \"@ref\": \"classes/derp\"\n        },\n        \"data\": {\n            \"test\": 1\n        },\n        \"ref\": {\n            \"@ref\": \"classes/derp/101192216816386048\"\n        },\n        \"ts\": 1432763268186882\n    }"
    val parsed = json.readValue[Instance](toDeserialize, classOf[Instance])
    parsed shouldBe Instance(Ref("classes/derp/101192216816386048"), Ref("classes/derp"), 1432763268186882L, ObjectV("test" -> 1L))
  }

  it should "deserialize a query response with a literal object" in {
    import Values._
    val toDeserialize = "{\n\t\t\"ref\": {\n\t\t\t\"@ref\": \"classes/spells/93044099947429888\"\n\t\t},\n\t\t\"class\": {\n\t\t\t\"@ref\": \"classes/spells\"\n\t\t},\n\t\t\"ts\": 1424992618413105,\n\t\t\"data\": {\n\t\t\t\"objectField\": { \"@object\": {\n\t\t\t\t\"@ref\": \"classes/spells/93044099909681152\"\n\t\t\t}\n\t\t}\n\t} }"
    val parsed = json.readValue[Instance](toDeserialize, classOf[Instance])
    parsed shouldBe Instance(Ref("classes/spells/93044099947429888"), Ref("classes/spells"), 1424992618413105L, ObjectV("objectField" -> ObjectV("@ref" -> "classes/spells/93044099909681152")))
  }
}
