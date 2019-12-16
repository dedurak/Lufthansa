package com.eduvation.lufthansa;

import com.eduvation.lufthansa.APIObjects.AccessTokenObject;
import com.eduvation.lufthansa.APIObjects.ApiAirportResult;
import com.eduvation.lufthansa.APIObjects.ApiFlights;
import com.eduvation.lufthansa.APIObjects.ApiFlightResults;
import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.*;


public interface ApiService {
    /*
    * retrofit callback AuthToken for further usage
    * */
    @FormUrlEncoded
    @POST("oauth/token")
    Single<Response<AccessTokenObject>> getAccessToken(@Field("client_id") String key,
                                                       @Field("client_secret") String secret,
                                                       @Field("grant_type") String credentials);

    /*
    * api callback search for flight details by flightnumber
    * */
    @GET("operations/flightstatus/{flightNumber}/{date}")
    Single<Response<ApiFlightResults>> getFlightByFlightNumber(@Path("flightNumber") String flightNumber,
                                                               @Path("date") String date,
                                                               @Header("Authorization") String authorization,
                                                               @Header("Accept") String values);

    /*
    * api callback search for flight details by flight route
    * */
    @GET("operations/flightstatus/route/{origin}/{destination}/{date}")
    Single<Response<ApiFlightResults>> getFlightsByRoute(@Path("origin") String origin,
                                                        @Path("destination") String destination,
                                                        @Path("date") String date,
                                                        @Header("Authorization") String authorization,
                                                        @Header("Accept") String values);

    /*
    * fetch airport information belonging to airport code
    * */
    @GET("mds-references/airports/{airportcode}")
    Single<Response<ApiAirportResult>> getAirport(@Path("airportcode") String code,
                                                @Query("lang") String language,
                                                @Header("Authorization") String authorization);


    @GET("operations/flightstatus/departures/{origin}/{dateTime}")
    Single<Response<ApiFlights>> getDepartures(@Path("origin") String departureOrigin,
                                               @Path("dateTime") String date,
                                               @Query("offset") int offset,
                                               @Query("limit") int limit,
                                               @Header("Authorization") String authorization,
                                               @Header("Accept") String values);

    @GET("operations/flightstatus/arrivals/{origin}/{dateTime}")
    Single<Response<ApiFlights>> getArrivals(@Path("origin") String arrivalOrigin,
                                             @Path("dateTime") String date,
                                             @Query("offset") int offset,
                                             @Query("limit") int limit,
                                             @Header("Authorization") String authorization,
                                             @Header("Accept") String values);
}
