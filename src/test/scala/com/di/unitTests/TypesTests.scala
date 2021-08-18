package com.di.unitTests

import com.di.types.PollutionLevel
import com.di.types._
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AnyWordSpecLike

class TypesTests extends AnyWordSpecLike {

  "PollutionLevel operations" must {

    "stringify correctly" in {
      val levelOne = new PollutionLevel(Some(10))
      val levelTwo = new PollutionLevel(None)
      val levelThree = new PollutionLevel(Some(300))

      levelOne.toString shouldBe "10"
      levelTwo.toString shouldBe "0"
      levelThree.toString shouldBe "100"
    }

    "combine correctly" in {
      val levelOne = new PollutionLevel(Some(0))
      val levelTwo = new PollutionLevel(Some(40))
      val levelThree = new PollutionLevel(Some(100))

      levelOne.combineWith(levelTwo).value shouldBe Some(16)
      levelThree.combineWith(levelOne).value shouldBe Some(40)
    }

    "+ / - correctly" in {
      val levelOne = new PollutionLevel(Some(0))

      (levelOne - 100).value shouldBe Some(0)
      (levelOne + 100).value shouldBe Some(100)
      (levelOne + 120).value shouldBe Some(100)
      (levelOne + 10).value shouldBe Some(10)
    }

    "apply PolLevel type correctly" in {
      val levelOne = new PollutionLevel(Some(1))
      val levelTwo = new PollutionLevel(Some(25))
      val levelThree = new PollutionLevel(Some(40))
      val levelFour = new PollutionLevel(Some(67))
      val levelFive = new PollutionLevel(Some(97))

      levelOne.description shouldBe `Clear`
      levelTwo.description shouldBe `Low`
      levelThree.description shouldBe `Medium`
      levelFour.description shouldBe `High`
      levelFive.description shouldBe `Critical`

    }
  }

}
//#full-example
