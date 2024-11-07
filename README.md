# Family Map Client

This project is part of the BYU CS240 course, designed to build an Android app that provides users with a geographical view of their family history using data from a Family Map server. The app leverages Google Maps for visualizing family events and relationships, connecting users to their historical roots through an intuitive and interactive interface.

## Project Goals
The Family Map Client project covers key concepts in:
- Object-Oriented Design
- User Interface Programming
- Native Android Development
- Web API integration
- Unit Testing

## Application Overview
The Family Map Client includes six main views to help users explore family events:
- **Main Activity**: Initial view, displays login and top-level map
- **Login Fragment**: Allows user login and registration
- **Event Activity**: Shows maps with detailed event markers
- **Person Activity**: Displays a person’s details and relationships
- **Settings Activity**: Lets users manage app settings
- **Filter Activity**: Controls which events are shown on the map
- **Search Activity**: Provides a search interface for people and events

## Key Features
### Main Activity
- Contains a **Login Fragment** for initial login and registration.
- Displays a **Map Fragment** showing family events upon successful login.

### Map Fragment
- An interactive map showing events from a user’s family history.
- Filters events based on settings in **Settings** and **Filter** activities.
- Displays event details, with clickable markers representing different event types.

### Person Activity
- Shows person’s details and lists their relationships and life events.
- Click on relationships or events to view further details in **Event** and **Person** activities.

### Event Activity
- Displays detailed information for a selected event on the map.
- Centered on the selected event, with map interaction and event relationship lines.

### Settings Activity
- Options for logout, data re-sync, map type selection, and enabling/disabling relationship lines.

### Filter Activity
- Allows users to control event visibility by event type, gender, and family side.

### Search Activity
- Provides search capabilities for people and events.
- Search results include both people and events, navigable to **Person** or **Event** activities.

  
## File Structure
- Find all java files in **main/java**
- Find all resource files in **main/res**
- Find .xml file in **main**
