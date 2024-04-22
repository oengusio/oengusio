package app.oengus.application;

import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.entity.dto.DonationStatsDto;
import app.oengus.adapter.jpa.entity.Donation;
import app.oengus.exception.OengusBusinessException;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.exceptions.HttpException;
import com.paypal.orders.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationService {
    private final PayPalHttpClient payPalHttpClient;

    public Page<Donation> findForMarathon(final String marathonId, final int page, final int size) {
        return Page.empty();
    }

    public Order initDonation(final String marathonId, final Donation donation) {
        return new Order();
    }

    private Order createOrder(final MarathonEntity marathon, final String amount) {
        final OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        final Money money = new Money()
            .value(amount)
            .currencyCode(marathon.getDonationCurrency());

        final AmountWithBreakdown amountWithBreakdown = new AmountWithBreakdown()
            .value(amount)
            .currencyCode(marathon.getDonationCurrency())
            .amountBreakdown(new AmountBreakdown().itemTotal(money));

        final List<Item> items = List.of(new Item().name("Donation to " + marathon.getSupportedCharity())
            .quantity("1")
            .category("DIGITAL_GOODS")
            .unitAmount(money));

        final PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
            .amountWithBreakdown(amountWithBreakdown)
            .items(items);

        final Payee payee = new Payee();
        payee.email(marathon.getPayee());
        purchaseUnitRequest.payee(payee);
        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));

        final OrdersCreateRequest request = new OrdersCreateRequest()
            .requestBody(orderRequest);

        try {
            return this.payPalHttpClient.execute(request).result();
        } catch (final IOException e) {
            if (e instanceof final HttpException he) {
                // Something went wrong server-side
                LoggerFactory.getLogger(DonationService.class).error(he.getMessage());
                he.headers()
                    .forEach(
                        x -> LoggerFactory.getLogger(DonationService.class).error(x + " :" + he.headers().header(x)));
            }
            throw new OengusBusinessException("ERROR_DONATION_CREATION");
        }
    }

    public void approveDonation(final String marathonId, final String orderId) {

    }

    public void deleteDonation(final String orderId) {
        //
    }

    public DonationStatsDto getStats(final String marathonId) {
        return new DonationStatsDto();
    }

    private void sendDonationEvent(final String url, final Donation donation) {
        //
    }

}
