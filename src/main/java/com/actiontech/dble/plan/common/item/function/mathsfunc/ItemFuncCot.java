package com.actiontech.dble.plan.common.item.function.mathsfunc;

import com.actiontech.dble.plan.common.item.Item;
import com.actiontech.dble.plan.common.item.function.ItemFunc;
import com.actiontech.dble.plan.common.item.function.primary.ItemDecFunc;

import java.math.BigDecimal;
import java.util.List;


public class ItemFuncCot extends ItemDecFunc {

    public ItemFuncCot(List<Item> args) {
        super(args);
    }

    @Override
    public final String funcName() {
        return "cot";
    }

    public BigDecimal valReal() {
        double db = args.get(0).valReal().doubleValue();
        if (args.get(0).isNull()) {
            this.nullValue = true;
            return BigDecimal.ZERO;
        }
        double tan = Math.tan(db);
        if (tan == 0.0) {
            signalDivideByNull();
            return BigDecimal.ZERO;
        } else {
            return new BigDecimal(1.0 / tan);
        }
    }

    @Override
    public ItemFunc nativeConstruct(List<Item> realArgs) {
        return new ItemFuncCot(realArgs);
    }
}