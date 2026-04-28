package ru.sennov.productranking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "scores", uniqueConstraints = {
        @UniqueConstraint(name = "uk_scores_product_window", columnNames = {"product_id", "window_days"})
})
public class Score extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "window_days", nullable = false)
    private Integer windowDays;

    @Column(name = "score", nullable = false)
    private Integer score;

    public Score() {
    }

    public Score(Product product, Integer windowDays, Integer score) {
        this.product = product;
        this.windowDays = windowDays;
        this.score = score;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getWindowDays() {
        return windowDays;
    }

    public void setWindowDays(Integer windowDays) {
        this.windowDays = windowDays;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
