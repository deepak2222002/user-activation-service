package web.minda.project.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import web.minda.project.entity.ActivationToken;
import web.minda.project.entity.LoginMaster;
import web.minda.project.repository.ActivationTokenRepository;
import web.minda.project.repository.LoginMasterRepository;

@Service
public class ActivationService {

	@Value("${app.login-url}")
	private String loginUrl;

	private final ActivationTokenRepository tokenRepository;
	private final LoginMasterRepository loginMasterRepository;
	private final EmailService emailService;

	public ActivationService(ActivationTokenRepository tokenRepository, EmailService emailService, LoginMasterRepository loginMasterRepository) {

		this.tokenRepository = tokenRepository;
		this.loginMasterRepository = loginMasterRepository;
		this.emailService = emailService;
	}

	public String activateAccount(String token) throws MessagingException {

		ActivationToken activationToken = tokenRepository.findByToken(token)
				.orElseThrow(() -> new RuntimeException("Invalid activation token"));

		// Already activated
		if (activationToken.isUsed()) {
			return activationSuccessPage("Account Already Activated",
					"Your account has already been activated. You can now log in.");
		}

		// Token expired
		if (activationToken.getExpiresAt().isBefore(LocalDateTime.now())) {

			return activationErrorPage("Activation Link Expired",
					"This activation link has expired. Please request a new activation link.");
		}
		
		// Mark token as used
		activationToken.setUsed(true);
		tokenRepository.save(activationToken);
		

		// Send activation success email
		emailService.sendActivationSuccessEmail(activationToken.getEmail());
		
		enableUser(activationToken.getEmail());

		// TODO:
		// Publish user-activated event to Kafka
		// User/Auth Service will set enabled=true

		return activationSuccessPage("Account Activated Successfully",
				"Your Job Portal account has been activated successfully. You can now log in.");
	}
	
	public void enableUser(String email) {

	    LoginMaster user = loginMasterRepository.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    user.setIsEnabled(true);

	    loginMasterRepository.save(user);
	}
	private String activationSuccessPage(String title, String message) {

		return """
				<!DOCTYPE html>
				<html>
				<head>
				    <title>%s</title>
				    <style>
				        body {
				            font-family: Arial, sans-serif;
				            background: #f4f7fb;
				            display: flex;
				            justify-content: center;
				            align-items: center;
				            height: 100vh;
				            margin: 0;
				        }

				        .card {
				            background: white;
				            padding: 40px;
				            border-radius: 12px;
				            text-align: center;
				            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
				            max-width: 450px;
				        }

				        .icon {
				            font-size: 60px;
				            color: #28a745;
				        }

				        h1 {
				            color: #222;
				        }

				        p {
				            color: #666;
				            font-size: 16px;
				            line-height: 1.6;
				        }

				        .login-btn {
				            display: inline-block;
				            margin-top: 20px;
				            padding: 12px 25px;
				            background: #007bff;
				            color: white;
				            text-decoration: none;
				            border-radius: 6px;
				        }
				    </style>
				</head>

				<body>

				    <div class="card">

				        <div class="icon">✓</div>

				        <h1>%s</h1>

				        <p>%s</p>

				        <a class="login-btn"
				           href="%s">
				            Go to Login
				        </a>

				    </div>

				</body>
				</html>
				""".formatted(title, title, message, loginUrl);
	}

	private String activationErrorPage(String title, String message) {

		return """
				<!DOCTYPE html>
				<html>
				<head>
				    <title>%s</title>
				    <style>
				        body {
				            font-family: Arial, sans-serif;
				            background: #f4f7fb;
				            display: flex;
				            justify-content: center;
				            align-items: center;
				            height: 100vh;
				            margin: 0;
				        }

				        .card {
				            background: white;
				            padding: 40px;
				            border-radius: 12px;
				            text-align: center;
				            box-shadow: 0 5px 20px rgba(0,0,0,0.1);
				            max-width: 450px;
				        }

				        h1 {
				            color: #dc3545;
				        }

				        p {
				            color: #666;
				            font-size: 16px;
				            line-height: 1.6;
				        }
				    </style>
				</head>

				<body>

				    <div class="card">

				        <h1>%s</h1>

				        <p>%s</p>

				    </div>

				</body>
				</html>
				""".formatted(title, title, message);
	}
}