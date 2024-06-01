# App Features and Implementation

## Home
<img width="153" alt="Screenshot 2024-06-01 at 4 17 30 PM" src="https://github.com/mar19a/HiFive/assets/84360137/a0b8b8b8-c468-45ba-b6ad-495bcdd76d75"> <img width="154" alt="Screenshot 2024-06-01 at 4 17 43 PM" src="https://github.com/mar19a/HiFive/assets/84360137/b38f967d-ceac-40df-9936-a592dc3a12bd">

### FollowAdapter.kt
- **Purpose:** Display a list of users that the current user follows in a RecyclerView.
- **Key Methods:**
  - `onCreateViewHolder()`: Inflates the custom layout for each item, including image and text views for user details.
  - `getItemCount()`: Returns the total number of users in the follow list.
  - `onBindViewHolder()`: Binds user data (profile image and name) to each ViewHolder using Glide for efficient image loading.

### PostAdapter.kt
- **Purpose:** Manage and display posts, including post images, user details, timestamps, and actions like liking or sharing.
- **Key Methods:**
  - `onCreateViewHolder()`: Prepares the view structure for each post item.
  - `getItemCount()`: Returns the number of posts to display.
  - `onBindViewHolder()`: Binds post data and interacts with Firebase to fetch and display user data, handle likes, and navigate based on post data.

### HomeFragment.kt
- **Purpose:** Central hub where posts and follows are displayed.
- **Key Methods:**
  - `loadProfileImage()`, `loadPosts()`, `loadFollows()`: Fetch data from Firebase and update adapters.
  - `onCreateView()`: Sets up RecyclerViews, binds data to adapters, and loads initial data.
  - `onOptionsItemSelected()`: Handles menu selections for navigation.

## Add a Comment

<img width="194" alt="Screenshot 2024-06-01 at 4 19 19 PM" src="https://github.com/mar19a/HiFive/assets/84360137/3dfe3bc4-ef69-44f9-aa40-2bd7df81da17">

### PostAdapter.kt and CommentAdapter.kt
- **Purpose:** Load and display comments for posts.
- **Key Methods:**
  - `onBindViewHolder()`: Initializes CommentAdapter, toggles comment section visibility, loads comments from Firebase when opened, and binds comment data to views.
  - `CommentViewHolder`: Sets user’s name, profile image, comment text, and relative time using the TimeAgo library.

## Add Post

<img width="489" alt="Screenshot 2024-06-01 at 4 19 58 PM" src="https://github.com/mar19a/HiFive/assets/84360137/2b9576ed-2879-4aed-ba11-25dbbb8a9914">

### AddFragment.kt
- **Purpose:** Provide a UI for adding different types of content.
- **Key Methods:**
  - `onCreateView()`: Sets up UI components and event handlers.
  - `postLinearLayout` OnClickListener: Fetches current location, creates intent to start PostActivity, and passes location data.

### PostActivity.kt
- **Purpose:** Allow users to create and publish posts.
- **Key Methods:**
  - `onCreate()`: Initializes form fields, buttons, and receives location data.
  - Image Selection: Uses `ActivityResultContracts.GetContent()` for selecting images and `uploadImage()` for uploading to Firebase.
  - Location Setup: Uses `ActivityResultContracts.StartActivityForResult()` for location selection.
  - Date and Time Pickers: Allow users to select date and time for events.
  - Event Handlers: Commit the new post to Firestore or cancel the operation.

## Look at Your Posts

### MyPostRvAdapter.kt
- **Purpose:** Manage and display a list of posts by the current user in a RecyclerView.
- **Key Methods:**
  - `onCreateViewHolder()`: Sets up the ViewHolder for each post item.
  - `getItemCount()`: Returns the count of posts available for display.
  - `onBindViewHolder()`: Binds data to each ViewHolder, loading post images using Picasso.

### MyPostFragment.kt
- **Purpose:** Display all posts made by the current user.
- **Key Methods:**
  - `onCreateView()`: Sets up the RecyclerView with its adapter and layout manager, and initiates loading of user posts.
  - `loadUserPosts()`: Fetches posts from Firebase Firestore and updates the adapter’s data set.

## Look at Your Likes

### MyLikesFragment.kt
- **Purpose:** Display a list of posts that the user has liked.
- **Key Methods:**
  - `onCreateView()`: Sets up the fragment layout, initializes PostAdapter, and configures the RecyclerView.
  - `loadLikedPosts()`: Fetches liked post IDs from Firebase Firestore and retrieves post details using `fetchPostsByIds()`.

## Messaging

### MessageAdapter.kt
- **Purpose:** Manage the display of chat messages in a RecyclerView.
- **Key Methods:**
  - `onCreateViewHolder()`: Creates new views by inflating the message layout.
  - `onBindViewHolder()`: Binds data to each ViewHolder, setting message text, sender’s name, timestamp, and profile image.
  - `getItemCount()`: Returns the total number of items in the data set.
  - `updateMessages()`: Updates the dataset within the adapter and notifies it to refresh the view.

### ChatRoomFragment.kt
- **Purpose:** Handle the user interface for a chat session.
- **Key Methods:**
  - `onCreateView()`: Sets up the RecyclerView with its adapter and layout manager.
  - `onViewCreated()`: Loads messages and sets up a button listener for sending new messages.
  - `loadMessages()`: Fetches messages from Firebase Firestore in real-time.
  - `sendMessageToFirebase()`: Sends a new message to Firebase Firestore.

### MessageFragment.kt
- **Purpose:** Display a list of users that the current user can chat with.
- **Key Methods:**
  - `onCreateView()`: Inflates the layout and initializes the RecyclerView.
  - `loadUsers()`: Fetches a list of users that the current user is following.

### UserChatAdapter.kt
- **Purpose:** Manage the display of a list of users within a RecyclerView.
- **Key Methods:**
  - `updateUsers(newUsers, newUserIds)`: Updates the adapter’s data sources.
  - `UserViewHolder`: Holds references to UI components and sets up a click listener.
  - `onCreateViewHolder(parent, viewType)`: Inflates the layout for individual components.
  - `onBindViewHolder(holder, position)`: Binds data to each ViewHolder.
  - `getItemCount()`: Returns the total number of items in the adapter.

## Profile

### ProfileFragment.kt
- **Purpose:** Provide a space for users to view and edit their profiles.
- **Key Methods:**
  - `onCreateView()`: Sets up UI components and listeners for user interactions.
  - `setupViewPager()`: Initializes a ViewPagerAdapter for managing tabs within the profile.
  - `logoutUser()`: Handles user logout and redirects to LoginActivity.
  - `updateUserProfile()`: Fetches and displays user information from Firebase Firestore.

### SignUpActivity.kt
- **Purpose:** Register new users or update existing profiles.
- **Key Methods:**
  - `onCreate()`: Sets up the activity layout and initializes form fields and buttons.
  - `registerForActivityResult()`: Manages image picker results and uploads profile images to Firebase Storage.

### LoginActivity.kt
- **Purpose:** Handle user authentication.
- **Key Methods:**
  - `onCreate()`: Sets up the activity layout and initializes input fields and buttons.
  - `login()`: Authenticates the user using Firebase Auth.

## Search

### SearchFragment.kt
- **Purpose:** Search through users the current user is following and navigate to AddUserActivity to follow new users.
- **Key Components:**
  - RecyclerView with SearchAdapter: Displays user data with a toggle button to unfollow users.

### AddUserActivity.kt
- **Purpose:** Connect with nearby users using Google Play services Nearby Connections API and goQR.me QR code API.
- **Key Methods:**
  - Broadcasting user’s device using NearbyConnectionsAPI.
  - Handling connection requests and sending user data via shaking the device or pressing a button.
  - Populating the SearchAdapter RecyclerView with followed/unfollowed users.

## Maps

### MapsActivity
- **Purpose:** Add a location to an event post.
- **Key Methods:**
  - Inflates `activity_maps` layout using Google Maps API.
  - Centers the map around the user’s last known location.
  - Allows selecting a location and sending it back to PostActivity.

### MapsFragment
- **Purpose:** Display a map with event locations and filter options.
- **Key Methods:**
  - Inflates `fragment_maps` layout using Google Maps API.
  - Centers the map around the user’s last known location.
  - Populates the map with event markers from Firestore.

### MapsViewModel
- **Purpose:** Store the user’s last location for use in MapsActivity and MapsFragment.

## APIs Used

- **goQR.me API:** Generates QR codes from data.
- **NearbyConnections API:** Manages local communication technologies for connecting nearby devices.
- **Google Maps API:** Displays maps and handles location selection.

## Roadblocks/Surprises/Workarounds

- **Connectivity Requirements:** Difficulty finding a suitable connection technology; resolved using NearbyConnections API.
- **Post Timestamps:** Ensured all new posts include timestamps.
- **Loading Posts:** Used Firestore’s pagination capabilities and lazy loading for better performance.
- **Location Data Integration:** Ensured accurate location capture and clear UI/UX flow in PostActivity.
- **Firebase Firestore Queries:** Optimized for efficiency with indexes and fine-tuned queries.
- **Live Messaging and Comments:** Implemented using Firebase Firestore’s real-time capabilities.
- **Device Compatibility:** Employed responsive design principles and extensive testing.
- **Maps/Location Synchronization:** Used a combination of intents and ViewModel for accurate data passing.

## Future Work

- **User Interface Refinement:** Based on user feedback, implement Material design components and transitions.
- **Real-time Functionalities:** Add instant notifications for new posts or updates.
- **Advanced Search and Filtering:** Implement search by keywords and filtering by date.
- **Expanded Social Functionalities:** Add tagging users in comments/posts and private groups/forums.
- **User Behavior Analytics:** Implement analytics to track user behavior and feedback mechanisms.
- **Offline Functionality:** Cache data locally for offline access.
- **Integration with Other APIs:** Enrich functionalities with weather services, AI for captions, etc.
- **Enhanced Security Features:** Implement one-time tokens for user data transfer.

## Bonus Features Implemented

- **Device Permissions:** Properly requested and handled.
- **Fragment Communication:** Utilized proper interfaces.
- **RecyclerView and CardView:** Used appropriately.
- **Menus:** Implemented effectively.
- **Gestures and Accelerometer:** Used correctly where needed.
- **Multi-locale Support:** Targeted multiple locales (e.g., English, Spanish).

## References

- [Implement Firebase Auth](https://firebase.google.com/docs/auth/web/start)
- [Retrieving Data](https://firebase.google.com/docs/database/admin/retrieve-data)
- [Read and Write Data](https://firebase.google.com/docs/database/android/read-and-write)
- [Upload files with Cloud storage](https://firebase.google.com/docs/storage/android/upload-files)
- [How to use Firebase Storage](https://stackoverflow.com/questions/39713875/how-to-use-firebase-storage)
- [Save images to the Firebase Storage](https://www.youtube.com/watch?v=CWSiX_KzP4o)
- [How to message users](https://www.youtube.com/watch?v=V9li7YP1NWg)
- [Login and registration](https://www.youtube.com/watch?v=tbh9YaWPKKs&list=PLlGT4GXi8_8dDK5Y3KCxuKAPpil9V49rN)
- [Firebase comments system](https://www.youtube.com/watch?v=5A8KlFUgcAg)
- [Instagram clone using firebase (Inspiration)](https://www.youtube.com/watch?v=3dsegOkif3Y&list=PLxefhmF0pcPnPuhlBPBq_FdG2GXpgrhVJ)
- [Google Maps implementation](https://www.youtube.com/watch?v=pOKPQ8rYe6g&list=PLHQRWugvckFrWppucVnQ6XhiJyDbaCU79)
- [Google Maps Platform Documentation](https://developers.google.com/maps/documentation/android-sdk)
- [Save data in database](https://www.youtube.com/watch?v=N4d4_zFR1nw&list=PL_QrnahGtqYAtYIoGod00kutYTvhq4HMU)
- [Nearby Connections documentation](https://developers.google.com/nearby/connections/overview)
