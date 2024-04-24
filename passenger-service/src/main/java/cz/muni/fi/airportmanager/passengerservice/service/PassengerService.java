package cz.muni.fi.airportmanager.passengerservice.service;

import cz.muni.fi.airportmanager.passengerservice.entity.Notification;
import cz.muni.fi.airportmanager.passengerservice.entity.Passenger;
import cz.muni.fi.airportmanager.passengerservice.model.CreatePassengerDto;
import cz.muni.fi.airportmanager.passengerservice.repository.PassengerRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped // This bean will be created once per application and live as long as the application lives
public class PassengerService {

    @Inject
    PassengerRepository passengerRepository;

    /**
     * Get list of all passengers
     *
     * @return list of all passengers
     */
    @WithTransaction
    public Uni<List<Passenger>> listAll() {
        return passengerRepository.listAll();
    }

    /**
     * Get passenger by id
     *
     * @param id passenger id
     * @return passenger with given id
     */
    @WithTransaction
    public Uni<Passenger> getPassenger(Long id) {
        return passengerRepository.findById(id);
    }

    /**
     * Get passengers for given flight id
     *
     * @param flightId flight id
     * @return list of passengers for given flight id
     */
    @WithTransaction
    public Uni<List<Passenger>> getPassengersForFlight(Long flightId) {
        return passengerRepository.findPassengersForFlight(flightId);
    }

    /**
     * Create a new passenger
     *
     * @param passenger passenger to create.
     * @return created passenger
     */
    @WithTransaction
    public Uni<Passenger> createPassenger(CreatePassengerDto passenger) {
        return passengerRepository.persist(Passenger.fromDto(passenger));
    }

    /**
     * Delete passenger
     *
     * @param id passenger id
     */
    @WithTransaction
    public Uni<Boolean> deletePassenger(Long id) {
        return passengerRepository.deleteById(id);
    }

    /**
     * Delete all passengers
     */
    @WithTransaction
    public Uni<Long> deleteAllPassengers() {
        return passengerRepository.deleteAll();
    }

    /**
     * Add notification to a passengers with given flight id
     *
     * @param flightId     flight id
     * @param notification notification to add
     */
    @WithTransaction
    public Uni<Void> addNotificationByFlightId(Long flightId, Notification notification) {
        return passengerRepository.addNotificationByFlightId(flightId, notification);
    }



    /**
     * Get all notifications for passenger
     *
     * @param passengerId passenger id
     * @return list of notifications for passenger
     */
    @WithTransaction
    public Uni<List<Notification>> findNotificationsForPassenger(Long passengerId) {
        return passengerRepository.findNotificationsForPassenger(passengerId);
    }
}
