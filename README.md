# File Management System

A Java-based file management application with a graphical user interface (GUI) for performing file operations such as creating, reading, updating, deleting, renaming, and navigating directories.  

---

## 🧩 Features
- Create, read, update, and delete text files
- Create and delete directories (including recursive delete)
- Rename files and directories
- Navigate between directories
- Real-time refresh of directory contents
- Error handling with user-friendly alerts
- JavaFX-based GUI with status updates and dialog feedback

---

## ⚙️ Technologies Used
- **Language/SDK:** OpenJDK 25
- **Framework:** JavaFX 25.0.1
- **Libraries:** Java NIO (for file operations)
- **IDE:** IntelliJ IDEA

---

## 🏗️ Build and Run Instructions

### **Option 1 — Run in IntelliJ IDEA**
1. Open IntelliJ IDEA.
2. Go to **File → Open...** and select this project folder.
3. Configure your JavaFX SDK:
   - Go to **File → Project Structure → Libraries → Add → Java**
   - Select the `/lib` folder of your JavaFX SDK.
4. Open **Run → Edit Configurations** and add this to **VM Options**:
