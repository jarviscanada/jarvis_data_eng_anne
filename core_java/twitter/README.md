# Introduction
`TwitterCLI` is an command line application that allows its user to post, show, and delete Twitter posts using the Twitter REST API. Based on arguments given to the `TwitterCLI` application, the application sends POST and GET requests to the Twitter REST API. In return, the Twitter REST API processes them, and then sends its reply to these requests. These replies inform `TwitterCLI` if the Tweet was successfully posted and the posted Tweet content, the content of the Tweet to show, and confirmation if the Tweet was successfully deleted and the deleted Tweet content. 

This app development was an exercise to learn and apply Data Access Object (DAO) design patterns, model-view-controller (MVC) architecture, and multilayer architecture. It uses technologies such Maven to build the application and to manage external dependencies, Mockito for testing, and Spring framework to manage dependencies between layers.  
 
 # Design
 
 <p align="center"><img src="assets/uml_diagram.png" alt="Multilayered, Model-View-Controller architecture design of the TwitterCLI application. Illustrated are the dependencies and interface realizations, as well as the four hierarchy layers." />
 
 `TwitterCLI` is implemented using a bottom-up approach. 
 
 The above diagram illustrates the dependencies and interface realizations of the `TwitterCLI` application. Through the four layer architecture, the application converts a command line request to an HTTP request submitted to the Twitter REST API.
 
### Components
1. **Data Access Object (DAO)**: Establishes the connection to the Twitter REST API and facilitates data transfer. `TwitterHTTPHelper` creates a connection and constructs URIs (to be submitted to the API), and processes the HTTP responses. Data is accessed and stored through the `TwitterDao` instantiation. 
2. **Service**: Deals with the business logic. It checks that all of the data passed from application to Twitter REST API is valid. For example, the `service` layer will check that the Tweet text does not contain more than 140 characters and that Tweet IDs contain only digits and fall within the range allowed by the `long` number data type.
3. **Controller**: Deals with the command line arguments. It observes the command line arguments given to it, checks that the appropriate number of options follow, and calls the corresponding method. 
4. **Application**: Declares and instantiates all of the components. The application layer calls its run method, which parses the arguments and calls the appropriate controller method. It prints the Tweets the results. 