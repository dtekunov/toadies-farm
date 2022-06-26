package com.di.unitTests

import com.di.actors.logic.ToadsActorLogic
import com.di.domain.{Farm, GrownToad, Toad}
import com.di.types.{Hunger, PollutionLevel, Rarity}
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AnyWordSpecLike

class ToadsArithmeticsTests extends AnyWordSpecLike with ToadsActorLogic {


  "ToadsActorLogic" must {
    "work correctly on empty vectors" in {
      val noToads = Vector.empty[Toad]

      val someCannibalFarm = Farm("id1", "name1", "creative", isCannibal = true, 1)
      val someNonCannibalFarm = Farm("id2", "name2", "creative", isCannibal = false, 1)

      updateToadsAgeAndHunger(noToads) shouldBe Vector.empty[Toad]

      countDeadBodies(noToads) shouldBe 0L

      countPollutionLevelBasedOnDeadBodies(0L) shouldBe 0L

      calculateHungryToadsByFarmType(noToads, someCannibalFarm).hungryCannibals shouldBe Some(Vector.empty[Toad])
      calculateHungryToadsByFarmType(noToads, someNonCannibalFarm).hungryCannibals shouldBe None

      updateToadsPollutionLevel(noToads, 10) shouldBe Vector.empty[Toad]

      feastToads(noToads, noToads) shouldBe Vector.empty[Toad]
    }

    "feast toads correctly" in {
      val toad1 = GrownToad("id1", "farmname", "name", "", true, 10, isCannibal = false, Rarity("common"),
        "", None, Vector.empty[String], PollutionLevel(None), false, Hunger(0))
      val toad2 = GrownToad("id2", "farmname", "name", "", true, 10, isCannibal = false, Rarity("common"),
        "", None, Vector.empty[String], PollutionLevel(None), false, Hunger(0))
      val toad3 = GrownToad("id3", "farmname", "name", "", true, 10, isCannibal = false, Rarity("common"),
        "", None, Vector.empty[String], PollutionLevel(None), false, Hunger(0))
      val toad4 = GrownToad("id4", "farmname", "name", "", true, 10, isCannibal = false, Rarity("common"),
        "", None, Vector.empty[String], PollutionLevel(None), false, Hunger(0))

      val cannibal1 = GrownToad("id5", "farmname", "name", "", true, 10, isCannibal = true, Rarity("common"),
        "", None, Vector.empty[String], PollutionLevel(None), false, Hunger(25))
      val cannibal2 = GrownToad("id6", "farmname", "name", "", true, 10, isCannibal = true, Rarity("common"),
        "", None, Vector.empty[String], PollutionLevel(None), false, Hunger(29))
      val cannibal3 = GrownToad("id7", "farmname", "name", "", true, 10, isCannibal = true, Rarity("common"),
        "", None, Vector.empty[String], PollutionLevel(None), false, Hunger(25))

      val noToads = Vector.empty[Toad]
      val noCannibals = Vector.empty[Toad]

      val singleToad = Vector(toad1)
      val singleCannibal = Vector(cannibal1)

      val manyToads = Vector(toad1, toad2, toad3, toad4)
      val manyCannibals = Vector(cannibal1, cannibal2, cannibal3)

      feastToads(noToads, singleCannibal) shouldBe Vector(cannibal1)

      feastToads(noToads, manyCannibals) shouldBe Vector(cannibal1.feedCannibal)

      feastToads(manyToads, noCannibals) shouldBe manyToads

      feastToads(singleToad, singleCannibal) shouldBe Vector(cannibal1.feedCannibal)

      feastToads(manyToads, singleCannibal) shouldBe Vector(cannibal1.feedCannibal, toad2, toad3, toad4)

      feastToads(manyToads, manyCannibals) shouldBe
        Vector(cannibal1.feedCannibal, cannibal2.feedCannibal, cannibal3.feedCannibal, toad4)

    }
  }
}
