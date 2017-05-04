package com.github.lpedrosa.todo.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.github.lpedrosa.todo.actor.server.message.reply.Entry;
import com.github.lpedrosa.todo.actor.server.message.request.AddEntry;
import com.github.lpedrosa.todo.actor.server.message.request.GetEntry;
import com.google.common.collect.ImmutableCollection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import scala.concurrent.duration.Duration;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class TodosTests {

    private static ActorSystem system;
    private static Todos todos;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
        todos = new Todos(system);
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
    }

    @Test
    public void shouldAlwaysGetSameActorForName() throws Throwable {
        ActorRef bob = todos.listFor("bob");
        ActorRef sameBob = todos.listFor("bob");

        assertEquals(bob, sameBob);
    }

    @Test
    public void serverShouldWorkProperly() throws Throwable {
        ActorRef bob = todos.listFor("bob");

        final LocalDate time = LocalDate.now();
        final String task = "Do the dishes";

        AddEntry add = new AddEntry(time, task);
        bob.tell(add, ActorRef.noSender());

        TestKit probe = new TestKit(system);

        GetEntry get = new GetEntry(time);
        bob.tell(get, probe.getRef());

        Entry reply = probe.expectMsgClass(Duration.apply(1, TimeUnit.SECONDS),
                Entry.class);

        ImmutableCollection<String> tasks = reply.getTasks();
        assertEquals(1, tasks.size());

        for (String t : tasks) {
            assertEquals(task, t);
        }
    }

}
