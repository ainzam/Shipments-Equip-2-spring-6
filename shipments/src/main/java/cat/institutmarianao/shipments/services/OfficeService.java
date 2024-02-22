package cat.institutmarianao.shipments.services;

import java.util.List;

import cat.institutmarianao.shipments.model.Office;

public interface OfficeService {
	Office getById(Long id);

	List<Office> getAllOffices();
}