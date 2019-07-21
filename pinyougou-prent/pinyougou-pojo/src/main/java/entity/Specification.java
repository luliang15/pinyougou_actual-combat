package entity;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName:Specification
 * @Author：Mr.lee
 * @DATE：2019/06/24
 * @TIME： 17:46
 * @Description: TODO
 */
public class Specification implements Serializable {

    private TbSpecification specification;//一个规格

    private List<TbSpecificationOption> optionList;//多个规格选项

    public TbSpecification getSpecification() {
        return specification;
    }

    public void setSpecification(TbSpecification specification) {
        this.specification = specification;
    }

    public List<TbSpecificationOption> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<TbSpecificationOption> optionList) {
        this.optionList = optionList;
    }
}
