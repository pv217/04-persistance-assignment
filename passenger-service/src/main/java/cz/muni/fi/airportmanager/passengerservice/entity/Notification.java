package cz.muni.fi.airportmanager.passengerservice.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Entity;

import java.util.Objects;

@Entity
// TODO make this class an active record entity
// TODO Add field message (String) and passengerId (Long)
public class Notification{
    // don't forget to override equals and hashCode
}
