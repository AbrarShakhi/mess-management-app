package com.github.abrarshakhi.mmap.home.data.repository;

import com.github.abrarshakhi.mmap.core.utils.Outcome;
import com.github.abrarshakhi.mmap.home.data.datasourse.DataSource;
import com.github.abrarshakhi.mmap.home.data.mapper.GroceryMapper;
import com.github.abrarshakhi.mmap.home.domain.model.GroceryBatch;
import com.github.abrarshakhi.mmap.home.domain.model.MonthYear;
import com.github.abrarshakhi.mmap.home.domain.repository.GroceryRepository;

import java.util.List;

public class GroceryRepositoryImpl implements GroceryRepository {
    private final DataSource ds;

    public GroceryRepositoryImpl(DataSource dataSource) {
        ds = dataSource;
    }


    public Outcome<Boolean, String> addGrocery(GroceryBatch groceryBatch) {
        try {
            // 1. Get current user and mess
            String userId = ds.getLoggedInUser().getUid();
            String messId = ds.getCurrentMessId();
            var messDto = ds.getMess(messId);

            if (messId == null || messId.isEmpty()) {
                return Outcome.failure("Current mess not set.");
            }

            if (messDto == null) {
                return Outcome.failure("Mess not found.");
            }

            // 2. Set batch properties
            groceryBatch.setTimestamp(System.currentTimeMillis());
            groceryBatch.setMessId(messId);
            groceryBatch.setUserId(userId);
            groceryBatch.setMonth(messDto.month);
            groceryBatch.setYear(messDto.year);

            // 3. Convert batch to list of DTOs
            var groceriesDto = GroceryMapper.toDtoListFromBatch(groceryBatch);

            // 4. Add groceries in Firestore batch
            ds.addGroceriesBatch(messId, groceriesDto);

            return Outcome.success(true);

        } catch (Exception e) {
            return Outcome.failure(e.getMessage());
        }
    }

    @Override
    public Outcome<MonthYear, String> findCurrentMonthYear() {
        try {
            var mess = ds.getMess(ds.getCurrentMessId());
            return MonthYear.newValidInstance(mess.month, mess.year);
        } catch (Exception e) {
            return Outcome.failure(e.getMessage());
        }
    }

    @Override
    public Outcome<List<GroceryBatch>, String> listGrocery(MonthYear monthYear) {
        try {
            var mess = ds.getMess(ds.getCurrentMessId());
            var groceriesDto = ds.getGroceries(mess.messId, monthYear.getMonth(), monthYear.getYear());
            var groceries = GroceryMapper.toDomainGrouped(groceriesDto);
            return Outcome.ok(groceries);
        } catch (Exception e) {
            return Outcome.failure(e.getMessage());
        }
    }

}
