module dk.martinu.ao.test {

    requires java.desktop;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;
    requires dk.martinu.ao.client;

    requires static org.jetbrains.annotations;

    opens dk.martinu.ao.test.event;
}