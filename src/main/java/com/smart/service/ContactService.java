package com.smart.service;

import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entity.Contact;
import com.smart.entity.User;

@Service
public class ContactService {
	
	 @Autowired
	    private UserRepository userRepository;
	

    @Autowired
    private ContactRepository contactRepository;
    
    public String upload(MultipartFile file, Integer numberOfSheet,String name) throws IOException, EncryptedDocumentException, InvalidFormatException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            if (numberOfSheet == null || numberOfSheet < 0 || numberOfSheet >= workbook.getNumberOfSheets()) {
                numberOfSheet = workbook.getNumberOfSheets();
            }
            User user=this.userRepository.findByEmail(name);

            for (int i = 0; i < numberOfSheet; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                for (Row row : sheet) {
                    // Process each row here
                   Contact contact= processRow(row);
                   contact.setUser(user);
                   contactRepository.save(contact);
                    System.out.println("Row "+row);
                }
            }
        } catch (IOException e) {
            // Handle IOException appropriately
            e.printStackTrace();
            return "Error occurred during file upload.";
        }
        return "File uploaded successfully.";
    }

    private Contact processRow(Row row) {
        Contact contact = new Contact();

        // Iterate over each cell in the row
        for (Cell cell : row) {
            int cellIndex = cell.getColumnIndex();
            String cellValue = getStringValueFromCell(cell); // Handle different cell types

            // Set values to Contact object based on cell index
            switch (cellIndex) {
                case 0:
                    contact.setName(cellValue);
                    break;
                case 1:
                    contact.setSecondName(cellValue);
                    break;
                case 2:
                    contact.setWork(cellValue);
                    break;
                case 3:
                    contact.setEmail(cellValue);
                    break;
                case 4:
                    contact.setPhone(cellValue);
                    break;
                case 5:
                    contact.setImage(cellValue);
                    break;
                case 6:
                    contact.setDescription(cellValue);
                    break;
                // Add cases for other cell indexes if needed
                default:
                    // Handle additional cell indexes if necessary
            }
            contactRepository.save(contact);
        }

        // Process the contact object or add it to a list as needed
        System.out.println("Contact: " + contact.toString());
        return contact;
    }

    
    private String getStringValueFromCell(Cell cell) {
        if (cell == null) {
            return ""; // Return empty string if cell is null
        }

        switch (cell.getCellTypeEnum()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // Handle numeric values based on your requirement
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // Evaluate formula and return string value
                return cell.getCellFormula();
            default:
                return "contacts "; // Return empty string for other cell types
        }
    }
}

	

	


