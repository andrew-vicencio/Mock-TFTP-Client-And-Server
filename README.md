# 3303-FileTransferSystem
File Transfer System for SYSC3303

# Layout

## File Purposes

1. Client
    1. Client: Main file, starts Command line listener
    2. ClientCommandLine: Listens to users command line inputs and triggers file transferees
    3. ClientThread: Handles sending and receiving files from the server
    
2. Error Simulator
    1. ErrorSimulator Handles the entirety of error simulation and being a middle man
    
3. Logger    Handles logging with various levels of verbosity
    1. Filter: Uses logLevel to determine if something should or should not be logged
    2. Logger: Stores a log level and allows other classes to log items
    3. LogLevels: Enum containing various logLevels for different verbosity modes
    4. Printer: Handles printing out various data types, for example a DatagramPacket
    
4. Packet    Handles parsing and creating packets.
    1. Packet: Allows a packet to be created from a string created by the byte array in a packet
    2. AcknowledgementPacket: Packet containing block number, used for acknowledging requests.
    3. ErrorPacket: Packet containing error code, used for communicating an error.
    4. ReadPacket: Packet containing a read request, used for starting a read.
    5. WritePacket: Packet containing a write request, used for starting a write.
    
5. Server
    1. Connection: Handles an individual connection to the server
    2. Listener: Listens for connections and spins up Connection threads.
    3. Server: Instantiates a listener
    
6. Tools    Utility classes shared among other classes.
    1. CommandLine tools used to create a simple command line interface
    2. PacketConstructor Utilities to construct a packet
    3. PacketDeconstructor Utilities to deconstruct a packet

## Setup
Run
    1. Select Run Configurations from the Run dropdown menu
    2. Create a new Launch Group
    3. Add Server.java, ErrorSimulator.java and Client.java to the Launch Group
    4. Select the new Launch Group from the run menu and click run
    
Command Line
    1. Enter send
    2. Enter read or write
    3. Enter file name


## Responsibilities

Server:
  * Jacob MacDonald
  * Benjamin Bichel
  
Logger:
  * Jacob MacDonald
  
Packet:
  * Jacob MacDonald
  
Tools:
  * Jacob MacDonald
  * Paul Hewson
  * Andrew Vicencio
  * Geoffrey Scornaienchi
  * Benjamin Bichel
  
Error Simulator:
  * Geoffrey Scornaienchi
  
Client:
  * Paul Hewson
  * Andrew Vicencio
  
ReadMe:
  * Jacob MacDonald
  
UML Class Diagram: 
  * Geoffrey Scornaienchi
