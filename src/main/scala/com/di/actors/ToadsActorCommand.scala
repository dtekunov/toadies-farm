package com.di.actors

import akka.actor.typed.{ActorRef, ActorSystem}
import com.di.domain.{GrownToad, Toad}
import com.di.jsonFormatters.rawdataFormats.RawGrownToad

/**
 * Toads actor request messages
 */
sealed trait ToadsActorCommand

final case class AddGrownToad(rawGrownToad: RawGrownToad,
                              system: ActorSystem[_],
                              replyTo: ActorRef[ToadsActorResponse]) extends ToadsActorCommand

final case class BornRandom(system: ActorSystem[_],
                            replyTo: ActorRef[ToadsActorResponse]) extends ToadsActorCommand

final case class GetAllToads(system: ActorSystem[_],
                             replyTo: ActorRef[ToadsActorResponse]) extends ToadsActorCommand

final case class KillToadById(id: String,
                              system: ActorSystem[_],
                              replyTo: ActorRef[ToadsActorResponse]) extends ToadsActorCommand

final case class GetToadById(id: String,
                             system: ActorSystem[_],
                             replyTo: ActorRef[ToadsActorResponse]) extends ToadsActorCommand

final case class UpdateToadsState(stopAt: BigInt,
                                  system: ActorSystem[_]) extends ToadsActorCommand

/**
 * Toads actor response messages
 */
sealed trait ToadsActorResponse

final case class DefaultToadsActorResponse(description: String,
                                           result: Option[String]) extends ToadsActorResponse

final case class MaybeToadsResponse(maybeToads: Option[Vector[Toad]]) extends ToadsActorResponse

final case class SingleToadResponse(maybeToad: Option[Toad]) extends ToadsActorResponse

