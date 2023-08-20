package org.eihq.quiltshow.model;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.eihq.quiltshow.repository.StringMapConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "judge_comments")
@Data
@EqualsAndHashCode
public class JudgeComment {

	@Id
    @GeneratedValue
    private Long id;
	
	
	@Convert(converter = StringMapConverter.class)
	@Column(length=30000)
	Map<String, String> judgeRemarks;
	
	Date createdDate;
	
	Date updatedDate;
	
	@JsonIgnore
	@OneToOne(mappedBy = "judgeComment")
    @EqualsAndHashCode.Exclude
	private Quilt quilt;


	public JudgeComment() {
		createdDate = Calendar.getInstance().getTime();
		updatedDate = Calendar.getInstance().getTime();;
	}
	
	public void updated() {
		setUpdatedDate(Calendar.getInstance().getTime());
	}
}
