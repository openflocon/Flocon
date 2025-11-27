### 📋 Configurable Data Tables (kotlin multi platform compatible)

<img width="1196" height="768" alt="tables" src="https://github.com/user-attachments/assets/ff3090fa-8f37-4138-a492-20b9159314af" />

In addition to dashboards, Flocon supports structured **data tables** that can be configured and updated by the mobile app.

These tables can be used to visualize:

- Lists of active users
- Items in memory or cache
- Custom logs or metrics
- Backend response simulations

Tables are interactive, scrollable, and they give developers and testers a straightforward way to inspect lists or collections in real time.

To create a dynamic row :
```kotlin
floconTable("analytics").log(
   "name" toParam "nameValue",
   "value1" toParam "value1Value",
   "value2" toParam "value2Value",
)
```