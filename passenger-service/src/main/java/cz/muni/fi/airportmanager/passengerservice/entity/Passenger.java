package cz.muni.fi.airportmanager.passengerservice.entity;

import cz.muni.fi.airportmanager.passengerservice.model.CreatePassengerDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO Add entity annotation
// TODO Add id field with generated value and it is a primary key
// TODO Add getters and setters
// TODO add relation to Notification entity (you will need to change the Notification entity as well)
public class Passenger {

}
