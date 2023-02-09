module dk.martinu.ao.client {

    requires java.desktop;
    requires dk.martinu.kofi;

    requires static org.jetbrains.annotations;

    exports dk.martinu.ao.client;
    exports dk.martinu.ao.client.event;
    exports dk.martinu.ao.client.core;
    exports dk.martinu.ao.client.ui;
    exports dk.martinu.ao.client.util;
    exports dk.martinu.ao.client.targets;
}