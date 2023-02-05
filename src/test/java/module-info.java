module dk.martinu.ao.test {

    requires dk.martinu.ao.client;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;
    requires static org.jetbrains.annotations;
    requires java.desktop;

    opens dk.martinu.ao.test.event;
}